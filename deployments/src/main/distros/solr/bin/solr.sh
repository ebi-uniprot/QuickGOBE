#! /bin/bash

##=======================================================================================
# Starts up a solr cloud environment
##=======================================================================================

set -eo pipefail
IFS=$'\n\t '

#readonly SOLR_JVM_PERF="-server -Xss50m -XX:+UseG1GC \
#-XX:+ParallelRefProcEnabled \
#-XX:MaxGCPauseMillis=250 \
#-XX:InitiatingHeapOccupancyPercent=40 \
#-XX:+AggressiveOpts \
#-XX:ConcGCThreads=4 \
#-XX:ParallelGCThreads=4 \
#-XX:MaxTenuringThreshold=8 \
#-XX:PretenureSizeThreshold=64m \
#-XX:G1ReservePercent=15 \
#-XX:+UseStringDeduplication \
#-XX:+PerfDisableSharedMem"

readonly SOLR_JVM_PERF="-server -Xss50m"
readonly SOLR_JVM_MON="-XX:+UnlockCommercialFeatures -XX:+FlightRecorder -XX:FlightRecorderOptions=loglevel=debug -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=3333 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false"
readonly PERMITTED_USER=("uni_qgo")
readonly ACTION="$1"
readonly PROFILE="$2"
readonly CORE_CONFIG="$3"

source "./common/common"

# ======= read the variables used by the control scripts ================================
source "../solr.variables" || {
    echo "Please create a file called, solr.variables, containing the necessary environment variables to setup solr cloud."
    exit 1
}

source "../zookeeper.variables" || {
    echo "Please create a file called, zookeeper.variables, containing the necessary environment variables to setup zookeeper."
    exit 1
}

# ========================= utility functions ===========================================
function show_help {
    cat<<EOF
  Usage: action [profile] [core_config]

  Argument description:
    action       => The action to perform on the solr cloud environment [start|stop|status]
    profile      => Indicates the profile of the solr cloud to startup, possible values [dev|test|prod]
    core_config  => Use this if you have more than one populated layout configuration for solr.

  Example: start dev -- starts up all solr cloud instances for the development environment
           start dev 8_shard -- starts solr cloud with the indexes shaved in the 8 shard setup.

  WARNING: Please be aware of who is using the machine before starting/stopping them.
EOF
}

function solr_vm_data_dir() {
   local hostname="$1"
   local core_config="$2"

   local vm_data_dir="$SOLR_DATA_DIR/$hostname"

   if [ ! -z "$core_config" ]; then
     vm_data_dir+="/$core_config"
   fi

   echo "$vm_data_dir"
}

# ====== check that the script has the right number of argument ========================
if [ "$#" -lt 1 ] || [ "$#" -gt 3 ]; then
   show_help
   exit 1
fi

# ====== check which environment will be started ========================================
if ! check_profile VALID_PROFILES $PROFILE; then
   echo "Input profile: '$PROFILE', not recognized. Allowable values: $(print_valid_profiles VALID_PROFILES)"
   exit 1
fi

# ======= check the right user runs this script =========================================
if ! user_can_execute_script $USER PERMITTED_USER; then
    echo "This service can only be run as user(s), '${PERMITTED_USER[@]}'"
    exit 1
fi

# ======= grab the virtual machines to startup ==========================================
if [ -z "${SOLR_HOSTS[$PROFILE]}" ]; then
   echo "No Solr hosts defined for profile '$PROFILE'"
   exit 1
fi

readonly VMS=${SOLR_HOSTS[$PROFILE]}

if [ -z "$VMS" ]; then
   echo "No VMs found for profile: $PROFILE"
   exit 1
fi

#start-up all vms for given profile
for vm in $VMS; do
   case $ACTION in
      start)
         echo "Starting: $vm"

         vm_data_dir=$(solr_vm_data_dir $vm $CORE_CONFIG)

         if [ ! -d "$vm_data_dir" ]; then
            mkdir "$vm_data_dir"
            cp ../solr.xml $vm_data_dir
         fi

         ssh "$USER"@"$vm" "$SOLR_LOCATION/bin/solr start -a "\"$SOLR_JVM_PERF $SOLR_JVM_MON\"" -cloud -s $vm_data_dir -p $SOLR_PORT -z $(zookeeper_hosts ${ZOO_HOSTS[$PROFILE]} $ZOO_PORT) -m $SOLR_MEM -noprompt"
         ;;
      stop)
         echo "Stopping: $vm"
         ssh "$USER"@"$vm" "$SOLR_LOCATION/bin/solr stop -p $SOLR_PORT"
         ;;
      status)
         echo "Checking status: $vm"
         #need to specify the collection I'm looking up
         #ssh "$USER"@"$vm" "$SOLR_LOCATION/bin/solr healthcheck -z "
         ;;
      *)

      echo "Unrecognized action: $ACTION"
      exit 1
   esac
done
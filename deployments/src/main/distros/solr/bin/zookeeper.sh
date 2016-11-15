#! /bin/bash

##=========================================================================================
# Starts up the zookeeper instances.
#
# Zookeeper is responsible for managing the Solr cloud nodes
##=========================================================================================

set -eo pipefail
IFS=$' '

source "./common/common"
source "../zookeeper.variables" || {
    echo "Please create a file called, zookeeper.variables, containing the necessary environment variables."
    exit 1
}

# ========================= CONSTANTS ==================================================
ACTION="$1"
PROFILE="$2"
VM="$3"
ZOOKEEPER_LOCATION=$(dirname $(which $0))/../dist/zookeeper/current
PERMITTED_USER=("uni_qgo")

SUPPORTED_ACTIONS=("start" "stop" "status")

# ========================= utility functions ===========================================
function show_help {
    cat<<EOF
  Usage: action profile [vm_name]

  Argument description:
    action       => The action to perform on the zookeeper service(s). Possible values: [$(join_by , "${SUPPORTED_ACTIONS[@]}")]
    profile      => Indicates the profile of the zookeeper instances to perform the action on. Possible values: [$(join_by , "${VALID_PROFILES[@]}")]
    vm_name      => The name of the virtual machine perform action on

  Example: dev -- starts up all development zookeper services
           dev ves-hx-c2 -- starts the development zookeeper service on ves-hx-c2
  WARNING: Please be aware of who is using the machine before starting/stopping them.
EOF
}

function action_is_supported {
    local action="$1"

    is_supported=1

    for valid_action in "${SUPPORTED_ACTIONS[@]}"; do
       if [ "$action" = "$valid_action" ]; then
          is_supported=0
          break
       fi
    done

    return $is_supported
}

action_on_zookeeper() {
   local vm="$1"
   local action="$2"

   echo "executing $vm:$action"
   ssh "$USER"@"$vm" "$ZOOKEEPER_LOCATION/bin/zkServer.sh $action $vm.cfg"
   echo "done"
}

# ======= check the right user runs this script =========================================
if ! user_can_execute_script $USER PERMITTED_USER; then
    echo "This service can only be run as user(s), '${PERMITTED_USER[@]}'"
    exit 1
fi

# ====== check that the script has the right number of arguments ========================
if [ "$#" -lt 2 ] || [ "$#" -gt 3 ]; then
   show_help
   exit 1
fi# ====== check chosen action is supported ========================
if ! action_is_supported $ACTION; then
   echo "Chosen action: $ACTION, is not supported. Supported values are: [$(join_by , ${SUPPORTED_ACTIONS[@]})]"
   exit 1
fi

# ====== check which environment will be started ========================================
if ! check_profile VALID_PROFILES $PROFILE; then
   echo "Input profile: $PROFILE, not recognized. Allowable values: $(print_valid_profiles VALID_PROFILES)"
   exit 1
fi

if [ ! -z $VM ] && ! vm_belongs_to_profile ${ZOO_HOSTS[$PROFILE]} $VM; then
  echo "Specified vm: $VM does not belong to profile: $PROFILE"
  exit 1
fi

for vm_in_profile in "${ZOO_HOSTS[$PROFILE]}"; do
   if [ -z $VM ] || [ "$vm_in_profile" = "$VM" ]; then
       case $ACTION in
           start)
             echo "Starting VM: $vm_in_profile"
             action_on_zookeeper $vm_in_profile $ACTION
             ;;
           stop)
             echo "Stopping VM: $vm_in_profile"
             action_on_zookeeper $vm_in_profile $ACTION
             ;;
           status)
             echo "Checking status: $vm_in_profile"
             action_on_zookeeper $vm_in_profile $ACTION
             ;;
           *)

           echo "Unrecognized action: $ACTION"
           exit 1
      esac
   fi
done


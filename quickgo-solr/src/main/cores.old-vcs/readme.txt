Directory old-vcs contains the schema/solrconfig solr files, that were originally in VCS.

These files differ to the ones found used by the server, ves-hx-cf (not sure why).

I diff'ed the contents of these files, for each core, and they are stored in this directory too.

It is important these solr core configuration files are in-sync with what is on the remote solr servers
otherwise unexpected behaviour is guaranteed.
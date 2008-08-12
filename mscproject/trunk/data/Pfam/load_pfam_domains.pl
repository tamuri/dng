#!/usr/bin/perl
#-----------------------------------------------------------
# load_pfam_domains
# 
# This script parses the files 'Pfam-A.seed' and 'Pfam-B' in the current
# directory. It loads data into the domain table:
# 
# domain - all Pfam domains, both Pfam-A and Pfam-B
# 
#-----------------------------------------------------------
use DBI;
use Cwd;

# Name of the files. The full Pfam-A file can also be used
@pfam_files = ("Pfam-A.seed", "Pfam-B");

# Set up the database connections and prepare the SQL statements
%datasource_vars = (
	host => "127.0.0.1",
	db => "dng",
	user => "root",
	passwd => "mysqlpwd",
	);

$datasource = "dbi:mysql:database=$datasource_vars{db};host=$datasource_vars{host};";
$dbh = DBI->connect($datasource, $datasource_vars{user}, $datasource_vars{passwd});

$sql_insert_domain = "INSERT INTO domain VALUES (?, ?, ?)";
$sth_insert_domain = $dbh->prepare($sql_insert_domain);

foreach $file (@pfam_files) {
	# Exit if we don't find the file 
	$file_path = Cwd::cwd() . "/" . $file; 
	unless (-e $file_path) {
		die ("File $file not found at $file_path\n");
	}
	
	open IN_FILE, '<', $file_path;
	
	$id_len = 0;
	$acc_len = 0;
	$desc_len = 0;
	
	while ($line = <IN_FILE>) {
		# We're interested in the following three lines:
		# #=GF ID   14-3-3
		# #=GF AC   PF00244.11
		# #=GF DE   14-3-3 protein
		
		if ($line =~ /#=GF\sID\s+(.*)$/) {
			$domain_id = $1;
			@domain_accession = <IN_FILE> =~ /^#=GF\sAC\s+(\w+)/; # Next line is accession
			@domain_description = <IN_FILE> =~ /^#=GF\sDE\s+(.+)$/; # Next line is description
			
			$sth_insert_domain->bind_param(1, $domain_id);
			$sth_insert_domain->bind_param(2, $domain_accession[0]);
			$sth_insert_domain->bind_param(3, $domain_description[0]);
			$sth_insert_domain->execute;
			
			print "Saved $domain_id, $domain_accession[0], $domain_description[0] \n";
		}	
	}
}

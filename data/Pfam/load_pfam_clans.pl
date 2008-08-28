#!/usr/bin/perl
#-----------------------------------------------------------
# load_pfam_clans
# 
# This script parses the file 'Pfam-C' in the current
# directory. It loads data into the 2 tables:
# 
# (1) clan - definition of pfam clan
#
# (2) domain_clan - association between domain and clan
# 
# We want to save the following lines:
# AC   CL0001.20
# ID   EGF
# DE   EGF superfamily
# ...
# MB   PF07645;
# MB   PF04863;
# ...
# // 
#-----------------------------------------------------------
use DBI;
use Cwd;

# Set up the database connections and prepare the SQL statements
%datasource_vars = (
	host => "127.0.0.1",
	db => "dng",
	user => "root",
	passwd => "mysqlpwd",
	);

$datasource = "dbi:mysql:database=$datasource_vars{db};host=$datasource_vars{host};";
$dbh = DBI->connect($datasource, $datasource_vars{user}, $datasource_vars{passwd});

$sql_insert_clan = "INSERT INTO clan VALUES (?, ?, ?)";
$sth_insert_clan = $dbh->prepare($sql_insert_clan);
$sql_insert_domain_clan = "INSERT INTO domain_clan VALUES (?, ?)";
$sth_insert_domain_clan = $dbh->prepare($sql_insert_domain_clan);

# Exit if we don't find the file 
$file_path = Cwd::cwd() . "/Pfam-C"; 
unless (-e $file_path) {
	die ("File Pfam-C not found at $file_path\n");
}

open IN_FILE, '<', $file_path;

while ($line = <IN_FILE>) {
	# if line matches accession
	if ($line =~ /^AC\s+\b(\w+)\b/) {
		$clan_ac = $1;
		$clan_id = (<IN_FILE> =~ /^ID\s+(.+)$/)[0]; # next line is id 
		$clan_description = (<IN_FILE> =~ /^DE\s+(.+)$/)[0]; # next line is description
		# new list of domains
		@domains = ();
	}
	# if line matches domain
	if ($line =~ /^MB\s+\b(\w+)\b/) {
		push @domains, $1;
	}
	# if line matches end of entry
	if ($line =~ /^\/\//) {
		# save the parsed definition
		print "Saving $clan_ac, $clan_id, $clan_description, " . join(",", @domains) . "\n";
		$sth_insert_clan->bind_param(1, $clan_ac);
		$sth_insert_clan->bind_param(2, $clan_id);
		$sth_insert_clan->bind_param(3, $clan_description);
		$sth_insert_clan->execute;
		
		foreach $domain_accession (@domains) {
			$sth_insert_domain_clan->bind_param(1, $domain_accession);
			$sth_insert_domain_clan->bind_param(2, $clan_ac);
			$sth_insert_domain_clan->execute;
		}
	}
}


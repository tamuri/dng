#!/usr/bin/perl
#-----------------------------------------------------------
# load_pdb_swissprot
# 
# This script parses the file 'pdbsws_chain.txt' in the current
# directory. It loads data into a single table:
# 
# (1) sequence_pdb - association of a swissprot code to a pdb entry 
#
# This mapping file is available at http://www.bioinf.org.uk/pdbsws/
#
#-----------------------------------------------------------
use DBI;
use Cwd;

# Exit if we don't file the swisspfam file 
$file_path = Cwd::cwd() . "/pdbsws_chain.txt"; 
unless (-e $file_path) {
	die ("File pdbsws_chain.txt not found at $file_path\n");
}

# Set up the database connections and prepare the SQL statements
%datasource_vars = (
	host => "127.0.0.1",
	db => "dng",
	user => "root",
	passwd => "mysqlpwd",
	);

$datasource = "dbi:mysql:database=$datasource_vars{db};host=$datasource_vars{host};";
$dbh = DBI->connect($datasource, $datasource_vars{user}, $datasource_vars{passwd});

$sth_insert_sequence_pdb = $dbh->prepare("INSERT INTO sequence_pdb VALUES (?, ?, ?)");

open IN_FILE, '<', $file_path;

while ($line = <IN_FILE>) {
	@entry = $line =~ /\b(\w+)\b/g;
	$sth_insert_sequence_pdb->bind_param(1, $entry[0]); # pdb_code
	$sth_insert_sequence_pdb->bind_param(2, $entry[1]); # chain_id
	$sth_insert_sequence_pdb->bind_param(3, $entry[2]); # sequence accession (swissprot)
	$sth_insert_sequence_pdb->execute;
	print "Saved $entry[0]\n";
}



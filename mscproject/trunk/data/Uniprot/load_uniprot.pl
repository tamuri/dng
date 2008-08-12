#!/usr/bin/perl
#-----------------------------------------------------------
# load_uniprot
# 
# This script parses the file 'uniprot_sprot.dat' in the current
# directory. It loads data into 3 tables:
# 
# (1) sequence - UniprotKB/Swissprot entries
# 
# (2) species 
#
# (3) sequence_accession - a lookup table of all accession numbers
# to the primary accession number
#-----------------------------------------------------------
use DBI;
use Cwd;

# Exit if we don't find file 
$file_path = Cwd::cwd() . "/uniprot_sprot.dat"; 
unless (-e $file_path) {
	die ("File uniprot_sprot.dat not found at $file_path\n");
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

$sth_insert_sequence = $dbh->prepare("INSERT INTO sequence VALUES (?, ?, ?)");
$sth_insert_species = $dbh->prepare("INSERT INTO species VALUES (?, ?)");
$sth_insert_accession = $dbh->prepare("INSERT INTO sequence_accession VALUES (?, ?)");

# Global variables
$species_id = 0;
%all_species = ();

open IN_FILE, '<', $file_path;

while ($line = <IN_FILE>) {
	if ($line =~ /^ID\s+\b(\w+)\b/) {
		$sequence_id = $1;
		@accessions = ();
	}
	
	if ($line =~ /^AC\s+(.*)$/) {
		push @accessions, $1 =~ /(\w{6})/g;
	}
	
	if ($line =~ /^OS\s+(.*)$/) {
		$species = $1;
	}
	
	if ($line =~ /^\/\//) {
		# Found end-of-record marker - save
		print $sequence_id . "\n" . join (",", @accessions) . "\n$species\n//\n";
		
		# Only save species if necessary, otherwise get the identifier
		if (exists($all_species{$species})) {
			$this_species_id = $all_species{$species};
		} else {
			# new architecture - save it!
			$this_species_id = ++$species_id;
			$all_species{$species} = $this_species_id;
			$sth_insert_species->bind_param(1, $this_species_id);
			$sth_insert_species->bind_param(2, $species);
			$sth_insert_species->execute;
		}
		
		# Save sequence
		$sth_insert_sequence->bind_param(1, $sequence_id);
		$sth_insert_sequence->bind_param(2, $accessions[0]);
		$sth_insert_sequence->bind_param(3, $this_species_id);
		$sth_insert_sequence->execute;
		
		# Save accessions (for normalization of other tables)
		foreach $a (@accessions) {
			$sth_insert_accession->bind_param(1, $accessions[0]);
			$sth_insert_accession->bind_param(2, $a);
			$sth_insert_accession->execute;
		}
	}
}



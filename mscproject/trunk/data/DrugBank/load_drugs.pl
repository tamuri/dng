#!/usr/bin/perl
#-----------------------------------------------------------
# load_drugs
# 
# This script parses the file 'drugcard_set.txt' in the current
# directory. It loads data into a two tables:
# 
# (1) drug - drug id, name
#
# (2) drug_target - drug_id, sequence_accession
#
#-----------------------------------------------------------
use DBI qw(:sql_types);
use Cwd;

# Exit if we don't file the swisspfam file 
$file_path = Cwd::cwd() . "/drugcard_set.txt"; 
unless (-e $file_path) {
	die ("File drugcard_set.txt not found at $file_path\n");
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

$sth_insert_drug = $dbh->prepare("INSERT INTO drug VALUES (?, ?, ?)");
$sth_insert_drug_target = $dbh->prepare("INSERT INTO drug_target VALUES (?, ?)");

open IN_FILE, '<', $file_path;

while ($line = <IN_FILE>) {
	if ($line =~ /BEGIN_DRUGCARD\s(DB\d{5})/) {
		$drug_id = $1;
		@drug_targets = ();
	}
	
	if ($line =~ /Generic_Name/) {
		$generic_name = <IN_FILE>;
		chomp($generic_name);
	}
	
	if ($line =~ /Drug_Type/) {
		$drug_type = <IN_FILE>;
		if (index($drug_type, "Approved") != -1) {
			$is_approved = 1;
		} else {
			$is_approved = 0;
		}
	}
	
	if ($line =~ /Drug_Target_(\d+)_SwissProt_ID/) {
		$target_swissprot_id = <IN_FILE>;
		chomp($target_swissprot_id);
		if (length($target_swissprot_id) && $target_swissprot_id ne "Not Available") {
			push @drug_targets, $target_swissprot_id;
		}
	}
	
	if ($line =~ /END_DRUGCARD/) {
		# Save entry
		$sth_insert_drug->bind_param(1, $drug_id);
		$sth_insert_drug->bind_param(2, $generic_name);
		$sth_insert_drug->bind_param(3, $is_approved);
		$sth_insert_drug->execute;
		
		foreach $target (@drug_targets) {
			$sth_insert_drug_target->bind_param(1, $drug_id);
			$sth_insert_drug_target->bind_param(2, $target);
			$sth_insert_drug_target->execute;
		}
		print "Saved $drug_id, $is_approved, " . join (";", @drug_targets) . "\n";
	}
}



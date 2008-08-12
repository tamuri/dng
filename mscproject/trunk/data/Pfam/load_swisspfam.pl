#!/usr/bin/perl
#-----------------------------------------------------------
# load_swisspfam
# 
# This script parses the file 'swisspfam' in the current
# directory. It loads data into 3 tables:
# 
# (1) sequence_domain - holds relationships of protein sequences 
# and domains
#
# (2) architecture - all architectures
#
# (3) sequence_architecture - holds relationships between sequences
# and architectures
#-----------------------------------------------------------

use DBI;
use Cwd;

# Exit if we don't file the swisspfam file 
$file_path = Cwd::cwd() . "/swisspfam"; 
unless (-e $file_path) {
	die ("File swisspfam not found at $file_path\n");
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

$sth_insert_architecture = $dbh->prepare("INSERT INTO architecture VALUES (?,?)");
$sth_insert_seq_arch = $dbh->prepare("INSERT INTO sequence_architecture VALUES (?, ?)");
$sth_insert_seq_domain = $dbh->prepare("INSERT INTO sequence_domain VALUES (?, ?)");

# Global variables
%all_architectures = ();
$architecture_id = 0;
$arch_len = 0;

open IN_FILE, '<', $file_path;

# For each line in the swisspfam file
while ($line = <IN_FILE>) {
	# If we're at the first line of a swisspfam entry
	# e.g. >104K_THEAN |=====================================| Q4U9M9.1 893 a.a.
	if ($line =~ /^>(\w+)\s+\|=+\|\s+\b(\w+)\b/) {
		# If we've got a sequence (and its domains) parsed already
		if (defined($protein_accession)) {
			# We need to save it before we start on the next sequence
			@architecture = ();
			
			# Sort the saved domains by sequence position to get the architecture
			foreach $key (sort {$a <=> $b} keys %domain_position) {
				push @architecture, $domain_position{$key};
			}
			
			$full_architecture = join ".", @architecture;

			# Unique list of domains
			@unique_domains = ();
			%seen = ();
			foreach $d (@architecture) {
				push(@unique_domains, $d) unless $seen{$d}++;
			}
			
			print "Saving $protein_accession\t$full_architecture\n";
			
			# if we have already saved this architecture
			if (exists($all_architectures{$full_architecture})) {
					$this_arch_id = $all_architectures{$full_architecture};
			} else {
				# new architecture - save it!
				$this_arch_id = ++$architecture_id;
				$all_architectures{$full_architecture} = $this_arch_id;
				$sth_insert_architecture->bind_param(1, $this_arch_id);
				$sth_insert_architecture->bind_param(2, $full_architecture);
				$sth_insert_architecture->execute;
			}
			
			# Save sequence_architecture entry
			$sth_insert_seq_arch->bind_param(1, $protein_accession);
			$sth_insert_seq_arch->bind_param(2, $this_arch_id);
			$sth_insert_seq_arch->execute;
			
			# Save sequence_domain entries
			foreach $d (@unique_domains) {
				$sth_insert_seq_domain->bind_param(1, $protein_accession);
				$sth_insert_seq_domain->bind_param(2, $d);
				$sth_insert_seq_domain->execute;
			}
			
			
			# Clear this entry
			undef(%domain_position);
			undef($protein_accession);
		}
		$protein_accession = $2;
		
	} elsif ($line =~ /^(?:[\w\-]+)\s+\d+[\s\-]+\(\d+\)\s\b(\w+)\b(?:.+)\s\s(.+)$/) {
		# Or if we're parsing a domain entry for a swisspfam entry
		# e.g. Tryp_alpha_amyl  1        ------------ (1760) PF00234.13 Protease inhibitor/seed storage/LTP family  35-99
		$pfam_ac = $1; # PF00234

		$positions = $2; # the rest of the entry: Protease inhibitor/seed storage/LTP family 35-99
		while ($positions =~ /\b(\d+)\-(\d+)\b/g) {
			$domain_position{$1} = $pfam_ac; # 35-99
		}
	}
}



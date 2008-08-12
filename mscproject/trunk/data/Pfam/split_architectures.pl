#!/usr/bin/perl
use DBI;

# Set up the database connections and prepare the SQL statements
%datasource_vars = (
	host => "127.0.0.1",
	db => "dng",
	user => "root",
	passwd => "mysqlpwd",
	);

$datasource = "dbi:mysql:database=$datasource_vars{db};host=$datasource_vars{host};";
$dbh = DBI->connect($datasource, $datasource_vars{user}, $datasource_vars{passwd});

$sth = $dbh->prepare("SELECT id, domains from architecture");
$sth->execute;

$sth_insert = $dbh->prepare("INSERT INTO domain_architecture VALUES (?, ?)");

while (($architecture_id, $architecture_domains) = $sth->fetchrow_array) {
	$count++;
	print "Splitting architecture $count: $architecture_domains\n";
	
	foreach $domain (split /\./, $architecture_domains) {
		$sth_insert->bind_param(1, $domain);
		$sth_insert->bind_param(2, $architecture_id);
		$sth_insert->execute;
		print "$architecture_id: $domain\n";
	}
}


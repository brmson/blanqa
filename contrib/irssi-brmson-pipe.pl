# This script communicates with blanqa via a pair of pipes.
#
# Create the pipes with:
#   mkfifo /tmp/brmson.ask; mkfifo /tmp/brmson.ans
#
# Configure blanqa to communicate via these pipes:
# (i) In collection-reader section, comment out everything and add instead:
#    inherit: phases.collection.pipe
#    askfile: /tmp/brmson.ask
# (ii) To the end of the file (pipeline section), append:
#      - inherit: phases.consumer.pipe
#        ansfile: /tmp/brmson.ans


use strict;
use warnings;

use Irssi;
use Irssi::Irc;
use IO::Handle;

use vars qw($VERSION %IRSSI);

$VERSION = '1.0';
%IRSSI = (
	authors => "Petr Baudis",
	contact => "pasky\@ucw.cz",
	name => "brmson",
	description => "brmson connector",
);

our ($askfd, $ansfd);

sub on_msg {
	my ($server, $message, $nick, $hostmask, $channel) = @_;
	my $mynick = $server->{nick};
	my $isprivate = !defined $channel;
	my $dst = $isprivate ? $nick : $channel;
	my $request;

	return if grep {lc eq lc $nick} split(/ /, Irssi::settings_get_str('brmson_ignore'));

	if ($message !~ s/^\s*$mynick[,:]\s*(.*)$/$1/i) {
		return;
	}

	print $askfd "$message\n";
	$askfd->flush();
	my $response = <$ansfd>;
	chomp $response;

	$server->send_message($dst, "$nick: $response", 0);
}

Irssi::signal_add('message public', 'on_msg');
Irssi::signal_add('message private', 'on_msg');

Irssi::settings_add_str('bot', 'brmson_ignore', '');

open $askfd, '>/tmp/brmson.ask' or die "/tmp/brmson.ask: $!";
open $ansfd, '</tmp/brmson.ans' or die "/tmp/brmson.ans: $!";

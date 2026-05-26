<?php
$type = strtolower($_GET['type'] ?? 'mod'); # mod is the default value
$channel = strtolower($_GET['channel'] ?? 'stable'); # stable is the default value
$filePath = './'.$type.($channel == 'stable' ? '' : '-'.$channel).'.jar'; # The path to the file

if (file_exists($filePath)) # Check if the file exists
    echo sha1_file($filePath); # Return the sha1 hash of the file
else
    echo -1; # Return -1 if the file does not exist

?>
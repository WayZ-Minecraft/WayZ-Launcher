<?php
function ScanDirectory($Directory, $tableau=false){
$slash = '';
	$MyDirectory = opendir($Directory) or die('Unable to open directory '.$Directory);
	while($Entry = @readdir($MyDirectory)) {
		if($Entry != '.' && $Entry != '..' && $Entry != 'index.php' && $Entry != ".htaccess" && !isSync($Entry)) { // Skip pointers
			if(is_dir($Directory.'/'.$Entry)) $slash = '/'; // If directory, add a slash to the end of the name,
            else $slash = '';
			$tableau[] = substr($Directory.'/'.$Entry, strlen(strstr($Directory.'/'.$Entry, '/', true))+1).$slash;
		}
		if(is_dir($Directory.'/'.$Entry) && $Entry != '.' && $Entry != '..' && !isSync($Entry)) $tableau = ScanDirectory($Directory.'/'.$Entry, $tableau);
	}
	closedir($MyDirectory);
	return $tableau;
}
 
Header('Content-type: text/xml');
$xml = new SimpleXMLElement('<xml/>');
$base = $xml->addChild('ListBucketResult');

foreach(ScanDirectory('.') as $key => $value){
    if(!isSync($value)) {
        $stat = stat($value);
        $content = $base->addChild('Contents');
        $content->addChild('Key', $value);
        if(is_dir($value) && $value != '.' && $value != '..'){
            $content->addChild('FFFF', 'Folder');
            $content->addChild('ETag', '"'.md5($value).'"');
            $content->addChild('Size', 0);
        } else {
            $content->addChild('Size', $stat['size']);
            $content->addChild('ETag', '"'.md5_file($value).'"');
        }
    }
}

function isSync($file) {
    return strpos($file, '.sync') !== false;
}

print($xml->asXML());
?>

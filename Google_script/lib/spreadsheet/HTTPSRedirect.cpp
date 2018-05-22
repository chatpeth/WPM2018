/*  HTTPS on ESP8266 with follow redirects, chunked encoding support
 *  Version 2.1
 *  Author: Sujay Phadke
 *  Github: @electronicsguy
 *  Copyright (C) 2017 Sujay Phadke <electronicsguy123@gmail.com>
 *  All rights reserved.
 *	Debug by Chatpeth
 *	Release 5/8/2018 
 */

#include "HTTPSRedirect.h"
#include "DebugMacros.h"

HTTPSRedirect::HTTPSRedirect(void) : _httpsPort(443){
  Init();
}

HTTPSRedirect::HTTPSRedirect(const int p) : _httpsPort(p){
  Init();
}

HTTPSRedirect::~HTTPSRedirect(){ 
}

void HTTPSRedirect::Init(void){
  _keepAlive = true;
  _printResponseBody = false;
  _maxRedirects = 10;
  _contentTypeHeader = "application/x-www-form-urlencoded";
}

// This is the main function which is similar to the method
// print() from WifiClient or WifiClientSecure
bool HTTPSRedirect::printRedir(void){
	//Serial.println("***********************************DB0xC0");
	
  unsigned int httpStatus;
  
  // Check if connection to host is alive
  if (!connected()){
    //Serial.println("Error! Not connected to host.");
    return false;
  }

  // Clear the input stream of any junk data before making the request
  while (available())
  {
	  //Serial.println("***********************************DB0xC1");
	  read();
	  //Serial.println("***********************************DB0xC2");
  }
  // Create HTTP/1.1 compliant request string
  // HTTP/1.1 complaint request packet must exist
  //Serial.println("***********************************DB0xC3");
  DPRINTLN(_Request);
  //Serial.println("***********************************DB0xC4");
  
  // Make the actual HTTPS request using the method 
  // print() from the WifiClientSecure class
  // Make sure the input stream is cleared (as above) before making the call
  //Serial.println("***********************************DB0xC5");
  print(_Request);
  //Serial.println("***********************************DB0xC6");
  // Read HTTP Response Status lines
  while (connected()) {
	  //Serial.println("***********************************DB0xC7");
    
    httpStatus = getResponseStatus();
	//Serial.println("***********************************DB0xC7.1");

    // Only some HTTP response codes are checked for
    // http://www.restapitutorial.com/httpstatuscodes.html
    switch (httpStatus){
      // Success. Fetch final response body
      case 200:
      case 201:
        {
		  //Serial.println("***********************************DB0xC8");
          // final header is discarded
          fetchHeader();
          
          #ifdef EXTRA_FNS
          printHeaderFields();
          #endif
    
          if (_hF.transferEncoding == "chunked")
            fetchBodyChunked();
          else
            fetchBodyUnChunked(_hF.contentLength);
          
          return true;
		  //Serial.println("***********************************DB0xC8.1");
        }
        break;
        
      case 301:
      case 302:
        {
		  //Serial.println("***********************************DB0xC9");
          // Get re-direction URL from the 'Location' field in the header
          if (getLocationURL()){
            //stop(); // may not be required

            _myResponse.redirected = true;
            
            // Make a new connection to the re-direction server
            if (!connect(_redirHost.c_str(), _httpsPort)) {
              //Serial.println("Connection to re-directed URL failed!");
              return false;
            }

            // Recursive call to the requested URL on the server
			//Serial.println("***********************************DB0xC9.1");
            return printRedir();

          }
          else{
            //Serial.println("Unable to retrieve redirection URL!");
            return false;
            
          }
        }  
        break;
        
      default:
        //Serial.print("Error with request. Response status code: ");
        //Serial.println(httpStatus);
        return false;
        break;
    } // end of switch

	//Serial.println("***********************************DB0xCA");
  }  // end of while
  //Serial.println("***********************************DB0xCB.1");
  return false;
  
}

// Create a HTTP GET request packet
// GET headers must be terminated with a "\r\n\r\n"
// http://stackoverflow.com/questions/6686261/what-at-the-bare-minimum-is-required-for-an-http-request
void HTTPSRedirect::createGetRequest(const String& url, const char* host){
  _Request =  String("GET ") + url + " HTTP/1.1\r\n" +
                          "Host: " + host + "\r\n" +
                          "User-Agent: ESP8266\r\n" +
                          (_keepAlive ? "" : "Connection: close\r\n") + 
                          "\r\n\r\n";

  return;
}

// Create a HTTP POST request packet
// POST headers must be terminated with a "\r\n\r\n"
// POST requests have 1 single blank like between the end of the header fields and the body payload
void HTTPSRedirect::createPostRequest(const String& url, const char* host, const String& payload){
	//Serial.println("***********************************DB0xB1");

  // Content-Length is mandatory in POST requests
  // Body content will include payload and a newline character
  unsigned int len = payload.length() + 1;
  
  _Request =  String("POST ") + url + " HTTP/1.1\r\n" +
                          "Host: " + host + "\r\n" +
                          "User-Agent: ESP8266\r\n" +
                          (_keepAlive ? "" : "Connection: close\r\n") +
                          "Content-Type: " + _contentTypeHeader + "\r\n" + 
                          "Content-Length: " + len + "\r\n" +
                          "\r\n" +
                          payload + 
                          "\r\n\r\n";
  //Serial.println("***********************************DB0xB2");

  return;
}


bool HTTPSRedirect::getLocationURL(void){

  bool flag;

  // Keep reading from the input stream till we get to 
  // the location field in the header
  flag = find("Location: ");

  if (flag){
    // Skip URI protocol (http, https, etc. till '//')
    // This assumes that the location field will be containing
    // a URL of the form: http<s>://<hostname>/<url>
    readStringUntil('/');
    readStringUntil('/');
    // get hostname
    _redirHost = readStringUntil('/');
    // get remaining url
    _redirUrl = String('/') + readStringUntil('\n');
  }
  else{
    DPRINT("No valid 'Location' field found in header!");
  }

  // Create a GET request for the new location
  createGetRequest(_redirUrl, _redirHost.c_str());

  DPRINT("_redirHost: ");
  DPRINTLN(_redirHost);
  DPRINT("_redirUrl: ");
  DPRINTLN(_redirUrl);
    
  return flag;
}

void HTTPSRedirect::fetchHeader(void){
  String line = "";
  int pos = -1;
  int pos2 = -1;
  int pos3 = -1;

  _hF.transferEncoding = "";
  _hF.contentLength = 0;
  
  #ifdef EXTRA_FNS  
  _hF.contentType = "";
  #endif
  
  while (connected()) {
    line = readStringUntil('\n');
    
    DPRINTLN(line);
    
    // HTTP headers are terminated by a CRLF ('\r\n')
    // Hence the final line will contain only '\r' 
    // since we have already till the end ('\n')
    if (line == "\r")
      break;

    if (pos < 0){
      pos = line.indexOf("Transfer-Encoding: ");
      if (!pos)
        // get string & remove trailing '\r' character to facilitate string comparisons
        _hF.transferEncoding = line.substring(19, line.length()-1);
    }
    if (pos2 < 0){
      pos2 = line.indexOf("Content-Length: ");
      if (!pos2)
        _hF.contentLength = line.substring(16).toInt();
    }
    #ifdef EXTRA_FNS
    if (pos3 < 0){
      pos3 = line.indexOf("Content-Type: ");
      if (!pos3)
        // get string & remove trailing '\r' character to facilitate string comparisons
        _hF.contentType = line.substring(14, line.length()-1);
    }
    #endif
      
  }

  return;
}

void HTTPSRedirect::fetchBodyUnChunked(unsigned len){
	//Serial.println("***********************************DB0x01");
  String line;
  DPRINTLN("Body:");

  while ((connected()) && (len > 0)) {
	  //Serial.println("***********************************DB0x02");
    line = readStringUntil('\n');
    len -= line.length();
    // Content length will include all '\n' terminating characters
    // Decrement once more to account for the '\n' line ending character
    --len;

	if (_printResponseBody)
	{
		//Serial.println(line);
		//Serial.println("***********************************DB0x03");
	}
      

    _myResponse.body += line;
    _myResponse.body += '\n';

  }
}

// Ref: http://mihai.ibanescu.net/chunked-encoding-and-python-requests
// http://fssnip.net/2t
void HTTPSRedirect::fetchBodyChunked(void){
	//Serial.println("***********************************DB0xE0");
  String line;
  int chunkSize;

  while (connected()){
	  //Serial.println("***********************************DB0xE1");
    line = readStringUntil('\n');

    // Skip any empty lines
    if (line == "\r")
      continue;
      
    // Chunk sizes are in hexadecimal so convert to integer
    chunkSize = (uint32_t) strtol((const char *) line.c_str(), NULL, 16);
    DPRINT("Chunk Size: ");
    DPRINTLN(chunkSize);

    // Terminating chunk is of size 0
    if (chunkSize == 0)
      break;
	//Serial.println("***********************************DB0xE2");
    while (chunkSize > 0){
		//Serial.println("***********************************DB0xE3");
      line = readStringUntil('\n');
	  if (_printResponseBody)
	  {
		  Serial.println("Status= ");
		  Serial.println(line);
	  }
        
      _myResponse.body += line;
      _myResponse.body += '\n';
      
      chunkSize -= line.length();
      // The line above includes the '\r' character 
      // which is not part of chunk size, so account for it
      --chunkSize;
	  //Serial.println("***********************************DB0xE4");
    }
    
    // Skip over chunk trailer
    
  }
  //Serial.println("***********************************DB0xE5");
  return;

}

unsigned int HTTPSRedirect::getResponseStatus(void){
  // Read response status line
  // ref: https://www.tutorialspoint.com/http/http_responses.htm
	//Serial.println("***********************************DB0xD0");
  unsigned int statusCode;
  String reasonPhrase;
  String line;
  unsigned int err_count = 0;

  unsigned int pos = -1;
  unsigned int pos2 = -1;

  // Skip any empty lines
  do{
	  //Serial.println("***********************************DB0xD1");
	 if (err_count > 1)
	{

		//ESP.restart();
		return false;
	}
    line = readStringUntil('\n');
	err_count = err_count + 1;
	//Serial.println("***********************************DB0xD1.1");

  }while(line.length() == 0);
  
  //Serial.println("***********************************DB0xD2");
  pos = line.indexOf("HTTP/1.1 ");
  pos2 = line.indexOf(" ", 9);
  
  if (!pos){
    statusCode = line.substring(9, pos2).toInt();
    reasonPhrase = line.substring(pos2+1, line.length()-1);
  }
  else{
    DPRINTLN("Error! No valid Status Code found in HTTP Response.");
    statusCode = 0;
    reasonPhrase = "";
  }
  
  _myResponse.statusCode = statusCode;
  _myResponse.reasonPhrase = reasonPhrase;
  
  DPRINT("Status code: ");
  DPRINTLN(statusCode);
  DPRINT("Reason phrase: ");
  DPRINTLN(reasonPhrase);
  //Serial.println("***********************************DB0xD3");
  return statusCode;
}

bool HTTPSRedirect::GET(const String& url, const char* host){
  return GET(url, host, _printResponseBody);
}

bool HTTPSRedirect::GET(const String& url, const char* host, const bool& disp){
  bool retval;
  bool oldval;

  // set _printResponseBody temporarily to argument passed
  oldval = _printResponseBody;
  _printResponseBody = disp;

  // redirected Host and Url need to be initialized in case a 
  // reConnectFinalEndpoint() request is made after an initial request 
  // which did not have redirection
  _redirHost = host;
  _redirUrl = url;
  
  InitResponse();
  
  // Create request packet
  createGetRequest(url, host);

  // Calll request handler
  retval = printRedir();

  _printResponseBody = oldval;
  return retval;
}

bool HTTPSRedirect::POST(const String& url, const char* host, const String& payload){
  return POST(url, host, payload, _printResponseBody);
}

bool HTTPSRedirect::POST(const String& url, const char* host, const String& payload, const bool& disp){
  bool retval;
  bool oldval;

  // set _printResponseBody temporarily to argument passed
  oldval = _printResponseBody;
  _printResponseBody = disp;

  //Serial.println("***********************************DB0x99");

  // redirected Host and Url need to be initialized in case a 
  // reConnectFinalEndpoint() request is made after an initial request 
  // which did not have redirection
  _redirHost = host;
  _redirUrl = url;
  
  //Serial.println("***********************************DB0xF0");
  InitResponse();
  //Serial.println("***********************************DB0xF1");
  // Create request packet
  createPostRequest(url, host, payload);
  //Serial.println("***********************************DB0xF2");

  // Call request handler
  //Serial.println("***********************************DB0xF3");
  retval = printRedir();
  //Serial.println("***********************************DB0xF4");

  _printResponseBody = oldval;
  //Serial.println("***********************************DB0xF5");
  return retval;
}

void HTTPSRedirect::InitResponse(void){
	//Serial.println("***********************************DB0xA0");
  // Init response data
  _myResponse.body = "";
  _myResponse.statusCode = 0;
  _myResponse.reasonPhrase = "";
  _myResponse.redirected = false;
  //Serial.println("***********************************DB0xA1");
}

int HTTPSRedirect::getStatusCode(void){
  return _myResponse.statusCode;
}

String HTTPSRedirect::getReasonPhrase(void){
  return _myResponse.reasonPhrase;
}

String HTTPSRedirect::getResponseBody(void){
  return _myResponse.body;
}

void HTTPSRedirect::setPrintResponseBody(bool disp){
  _printResponseBody = disp;
}

void HTTPSRedirect::setMaxRedirects(const unsigned int n){
  _maxRedirects = n;  // to-do: use this in code above
}

void HTTPSRedirect::setContentTypeHeader(const char *type){
  _contentTypeHeader = type;
}

#ifdef OPTIMIZE_SPEED
bool HTTPSRedirect::reConnectFinalEndpoint(void){
  // disconnect if connection already exists
  if (connected())
    stop();

  DPRINT("_redirHost: ");
  DPRINTLN(_redirHost);
  DPRINT("_redirUrl: ");
  DPRINTLN(_redirUrl);

  // Connect to stored final endpoint
  if (!connect(_redirHost.c_str(), _httpsPort)) {
    DPRINTLN("Connection to final URL failed!");
    return false;
  }

  // Valid request packed must already be
  // present at this point in the member variable _Request 
  // from the previous GET() or POST() request
  
  // Make call to final endpoint
  return printRedir();  
}
#endif

#ifdef EXTRA_FNS
void HTTPSRedirect::fetchBodyRaw(void){
  String line;

  while (connected()){
    line = readStringUntil('\n');
    if (_printResponseBody)
      //Serial.println(line);

    _myResponse.body += line;
    _myResponse.body += '\n';
  }
}

void HTTPSRedirect::printHeaderFields(void){
  DPRINT("Transfer Encoding: ");
  DPRINTLN(_hF.transferEncoding);
  DPRINT("Content Length: ");
  DPRINTLN(_hF.contentLength);
  DPRINT("Content Type: ");
  DPRINTLN(_hF.contentType);
}
#endif


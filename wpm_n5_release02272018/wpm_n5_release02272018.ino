#include <ESP8266WiFi.h>
#include <PubSubClient.h>
#include <time.h>
#include "HTTPSRedirect.h"
#include "DebugMacros.h"

#define SIM_MODE        // Define, If need to simulation pulse in.
#define ON 1
#define OFF 0
#define NID "5"
#define SW "sw" NID
#define ALM "Alarm" NID
#define T_MAX 1000000
#define MAX_CONNECT 100000
#define CLID "fff" NID
#define SHEET_NAME "\"แผ่น" NID "\", \"values\": "
#define URL_BASE "{\"command\": \"appendRow\",\"sheet_name\": " SHEET_NAME

// for stack analytics
extern "C" {
#include <cont.h>
  extern cont_t g_cont;
}

typedef struct
{
  int state;
  int pinIn;
  unsigned long duration;
  float power;
}structPhase;
// State = 0 if power is down, Other state = 1

// Phase A = GPIO2 = D4
// Phase B = GPIO4 = D2
// Phase C = GPIO5 = D1

// New

// Pa = GPIO5 =D1
// Pb = GPIO4 = D2
// Pc = GPIO14 = D5
structPhase phaseID[3] = { {1, 5, 0}, {1, 4, 0}, {1, 14, 0} };

//--> old structPhase phaseID[3] = { {1, 2, 0}, {1, 4, 0}, {1, 5, 0} };

int sw_status = ON;
int count_connect = 0;
char nodeID[5] = NID;
const char* ssid = "chp-lab";
const char* password = "0x00FF0000;";
const char* mqttServer = "m12.cloudmqtt.com";
const int mqttPort = 19574;
const char* mqttUser = "qonihivg";
const char* mqttPassword = "UNIemBQQpGw8";
//change this
const char* client_id = CLID;

unsigned long now;
long lastMsg = 0;

WiFiClient espClient;
PubSubClient client(espClient);

//--> ggsp
const char* host = "script.google.com";
// Replace with your own script id to make server side changes
// Link to script of google spread sheet
const char *GScriptId = "AKfycbyJW6L0XCxnaaUaIblDZWprteCtFoaRhPemmnIvFw3vR31t75co";
const int httpsPort = 443;
// echo | openssl s_client -connect script.google.com:443 |& openssl x509 -fingerprint -noout
const char* fingerprint = "‎46 b2 c3 44 9c 59 09 8b 01 b6 f8 bd 4c fb 00 74 91 2f ef f6";
// Fetch Google Calendar events for 1 week ahead
String url2 = String("/macros/s/") + GScriptId + "/exec?cal";
// Read from Google Spreadsheet
//String url3 = String("/macros/s/") + GScriptId + "/exec?read";
String payload_base =  URL_BASE;
String payload = "";
HTTPSRedirect* clientg = nullptr;
// used to store the values of free stack and heap
// before the HTTPSRedirect object is instantiated
// so that they can be written to Google sheets
// upon instantiation
unsigned int free_heap_before = 0;
unsigned int free_stack_before = 0;
// -->ggspe

void setup_wifi()
{ 
  // We start by connecting to a WiFi network
  printf("Node %s\r\nConnecting to %s\r\n", nodeID, ssid);
  
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED)
  {
    delay(100);
    printf(".");
  }

  printf("WiFi connected. IP address:\r\n");
  Serial.println(WiFi.localIP());

  //--> ggsp
  // Use HTTPSRedirect class to create a new TLS connection
  clientg = new HTTPSRedirect(httpsPort);
  clientg->setPrintResponseBody(true);
  clientg->setContentTypeHeader("application/json");
  printf("Connecting to %s\r\n", host);
  while( (clientg->connect(host, httpsPort)) != 1)
  {
    printf(".");
    delay(256);
  }
  printf("Connected");
  if (clientg->verify(fingerprint, host)) 
  {
    printf("Certificate match\r\n");
  }
  else 
  {
    printf("Certificate mis-match\r\n");
  }
  
  delete clientg;
  clientg = nullptr;
  //--> ggspe
}

void pubData()
{
    String ldrv;    
    String pubMsg;
    String power_str[3];
    String msg_topic;
    char power_topic[8];
    char Data[64];
    char power_payload[16];
    int i;

    // 3phase data
    for(i = 0; i < 3 ; i++)
    {
      float f = 0, p = 0;
      String almMsg;
      char alm[64];

      phaseID[i].power = p;
      printf("Phase %d: T = %d\r\n", i, phaseID[i].duration);
      
      // Check 0 < T < T_MAX to confirm power status    
      if(phaseID[i].duration != 0 && phaseID[i].duration < T_MAX)
      {
        // Power is back 
        if(phaseID[i].state == 0)
        {
          almMsg = "Node " + String(nodeID) + " Phase " + String(i) + " is up.\r\n";          
          almMsg.toCharArray(alm, almMsg.length() + 1);  
          Serial.print(almMsg);  
          client.publish(ALM, alm);
          // Clear blackout flag
          phaseID[i].state = 1;
        }

        //Calculate frequency
        f = 1000000.00/phaseID[i].duration; 
        //Calculate power 
        p = 60*f - 2;
        phaseID[i].power = p;
      }
      // Blackout
      else if(phaseID[i].duration == 0 || phaseID[i].duration > T_MAX)
      {
        // Approximate that P ~ 0
        phaseID[i].duration = 0;
        if(phaseID[i].state == 1)
        {
          almMsg = "Node " + String(nodeID) + " Phase " + String(i) + " is down.\r\n";      
          almMsg.toCharArray(alm, almMsg.length() + 1);
          client.publish("Alarm", alm);
          Serial.print(almMsg);
          // Set blackout flag
          phaseID[i].state = 0;                
        }
      } 

      printf("f= %.2f Hz\tp= %.2f W\r\n", f, p);

      // Old version msg
      ldrv = String(p);
      // message to public to mqtt brocker
      pubMsg = pubMsg +  ldrv + "_";

      //r/new version msg
      power_str[i] = String(p);
      power_str[i].toCharArray(power_payload, power_str[i].length() + 1);
      msg_topic = String(nodeID) + "_p" + String(i);
      msg_topic.toCharArray(power_topic, msg_topic.length() + 1);

      printf("Publish data topic %s\r\n", power_topic);
      client.publish(power_topic, power_payload);     
     }   
}

void callback(char* topic, byte* payload, unsigned int length)
{
  String topic_str = String(topic);

  if(topic_str == SW)
  {
    String payload_str;
    for(int i = 0; i < length; i++)
    {
      payload_str = payload_str + String((char)payload[i]);
    }

    if((payload_str == "1") || (payload_str == "true"))
    {
      printf("##################SW= on\r\n");
      sw_status = ON;
    }
    else if((payload_str == "0") || payload_str == "false")
    {
      printf("##################SW= of\r\n");
      sw_status = OFF;
    }
  }

  printf("Message arrived [%s] came into callback. The published message from Pi:\r\n", topic);
  for (int i = 0; i < length; i++)
  {
    Serial.print((char)payload[i]);
  }
  Serial.println();
}

void setup()
{
  int i;
  int timeZone = 7*3600;

  pinMode(LED_BUILTIN, OUTPUT);
  #ifndef SIM_MODE
  for(i = 0; i < 3; i ++)
  {
    // Set pin_in to measure pulse width
    pinMode(phaseID[i].pinIn, INPUT);
    printf("PinIn= %d\r\n", phaseID.pinIn);
  }
  #endif
  
  Serial.begin(115200);

  //--> ggsp
  free_heap_before = ESP.getFreeHeap();
  free_stack_before = cont_get_free_stack(&g_cont);
  //-->ggspe

  setup_wifi();
  
  client.setServer(mqttServer, mqttPort);
  client.setCallback(callback);
  
  
  configTime(timeZone, 0, "pool.ntp.org", "time.nist.gov");
  printf("Loading Time");
  while(!time(nullptr))
  {
    Serial.print(".");
    delay(256);
  }
  printf("\r\n");
  
}

//mqtt connection
void reconnect()
{

  // Loop until we’re reconnected
  while (!client.connected())
  {

    printf("Attempting MQTT connection... "); 
    // Attempt to connect
    if (client.connect(client_id, mqttUser, mqttPassword))
    {
      printf("connected.");    
      // … and resubscribe
      client.subscribe(nodeID);
      client.subscribe(SW);
      client.publish(nodeID, "Hello 505atk");
    }
    else
    {
      printf("failed, rc= ");
      Serial.print(client.state());
      printf(" ...try again in no longer\r\n");
      // Wait 0 - 1 seconds before retrying
      delay(random(1000));
      count_connect = count_connect + 1;
    }

    printf("count_connect= %d\r\n", count_connect);  
  }
}

void spreadsheet()
{
  static int error_count = 0;
  static int connect_count = 0;
  static bool flag = false;
  //configTime(7*3600, 0, "pool.ntp.org", "time.nist.gov");
  time_t now = time(nullptr);
  struct tm* p_tm = localtime(&now);
  String tmpf = String(p_tm->tm_mday) + "/" + String(p_tm->tm_mon + 1) + "/" + String(p_tm->tm_year + 1900) + "," + String(p_tm->tm_hour) + ":" + String(p_tm->tm_min) + ":" + String(p_tm->tm_sec);
  payload = payload_base + "\"" + phaseID[0].power + "," + phaseID[1].power + "," + phaseID[2].power + "," + tmpf + "\"}";
  printf("Time: %02d:%02d:%02d\r\n",p_tm->tm_hour, p_tm->tm_min, p_tm->tm_sec);

  if (!flag)
  {
    free_heap_before = ESP.getFreeHeap();
    free_stack_before = cont_get_free_stack(&g_cont);
    clientg = new HTTPSRedirect(httpsPort);
    flag = true;
    clientg->setPrintResponseBody(true);
    clientg->setContentTypeHeader("application/json");
    clientg->setTimeout(3000);
  }

  if (clientg != nullptr)
  {
    if (!clientg->connected())
    {
      printf("Connecting to %s\r\n", host);
      while(clientg->connect(host, httpsPort) == 0)
      {
        printf(".");
        delay(1);
      } 
    }
    printf("POST data to spreadsheet\r\n");
    if(clientg->POST(url2, host, payload))
    {
      connect_count++;
      printf("Count connect = %d\r\n", connect_count);
    }
    else
    {
      Serial.println("debug code 0x03");
      error_count = error_count + 1;
      DPRINT("Error-count while connecting: ");
      DPRINTLN(error_count);
    }
  }
  else
  {
    DPRINTLN("Error creating client object!");
    error_count = 5;
  }

  if (connect_count > MAX_CONNECT)
  {
    connect_count = 0;
  }

  if (error_count > 3)
  {
    error_count = 0;
    printf("Halting processor...\r\n"); 
    delete clientg;
    clientg = nullptr;
    flag = false;
    printf("Reset cause post error count = 3\r\n");
  }
}

void loop()
{
  //V2.2
  int i;
  
  
  // Check connection with mqtt status
  if (!client.connected() )
  {
    reconnect();
  }
  client.loop();  
  now = millis();
  
  // Upload everry 5 sec        
  if(abs( (now - lastMsg) > 5000) )
  {
    printf("Period = %d\r\n", now - lastMsg);
    lastMsg = now;

    #ifdef SIM_MODE
    int i;
    if(sw_status == ON)
    {
      
      for(i = 0; i < 3; i ++)
      {
        
        // 10 < P < 100 kW
        phaseID[i].duration = random(600, 6000);
      }
    }
    else if(sw_status == OFF)
    {
       printf("Restarting...\r\n");
       ESP.restart();
    } 
    #else
    if(sw_status == ON)
    {
      // Set DO = HIGH
    }
    else if(sw_status == OFF)
    {
      // Set DO = LOW
    }
    phaseID[0].duration = pulseIn(phaseID[0].pinIn, LOW) + pulseIn(phaseID[0].pinIn, HIGH);
    phaseID[1].duration = pulseIn(phaseID[1].pinIn, LOW) + pulseIn(phaseID[1].pinIn, HIGH);
    phaseID[2].duration = pulseIn(phaseID[2].pinIn, LOW) + pulseIn(phaseID[2].pinIn, HIGH);
    #endif
  
    // turn on LED
    digitalWrite(LED_BUILTIN, LOW);
    pubData();
    spreadsheet();
    // turn off LED
    digitalWrite(LED_BUILTIN, HIGH);

    // Clear old data
    for(i = 0; i < 3; i++)
    {
      phaseID[i].duration = 0; 
    }
  }  
    
  delay(random(5));  
}


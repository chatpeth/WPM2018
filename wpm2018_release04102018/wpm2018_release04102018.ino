// Cr. CHATPETH KENANAN
// Contact: eechatpeth@gmail.com

#include <ESP8266WiFi.h>
#include <PubSubClient.h>
#include <time.h>
#include "HTTPSRedirect.h"
#include "DebugMacros.h"

#define SIM_MODE        // Define, If need to simulation pulse in.
#define ON 1
#define OFF 0
#define NID "1"
#define SW "sw" NID
#define ALM "Alarm" NID
#define T_MAX 1000000
#define MAX_CONNECT 100000
#define CLID "fff" NID
#define NUM_PHASE 3
#define MAX_ERR 3
#define TIME_OUT 3000
#define SLOPE "m" NID
#define CONST "C" NID
#define ENCRIPT_TOPIC "fff" NID
#define LOG_SETTING "log_enable" NID
#define LOG_INTERVAL "log_interval" NID
#define POLLING_INTERVAL "polling_interval" NID
#define MAX_EQUATION_REQ 10
#define DEFAULT_SLOPE 60
#define DEFAULT_CONST -2
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

// Pa = GPIO5 =D1
// Pb = GPIO4 = D2
// Pc = GPIO14 = D5
structPhase phaseID[3] = { {1, 5, 0, 0}, {1, 4, 0, 0}, {1, 14, 0, 0} };

int sw_status = ON;
int count_connect = 0;
char nodeID[5] = NID;
const char* ssid = "atop802.11x";
const char* password = "atop3352";
//IPAddress ip(192, 168, 1, 140); //set static ip
//IPAddress gateway(192, 168, 1, 1); //set getteway
//IPAddress subnet(255, 255, 255, 0);//set subnet
const char* mqttServer = "m12.cloudmqtt.com";
const int mqttPort = 19574;
const char* mqttUser = "qonihivg";
const char* mqttPassword = "UNIemBQQpGw8";
const char* client_id = CLID;

unsigned long now;
long lastMsg = 0;
long lastLog = 0;
float m_slope = 0;
float C_const = 0;
int log_setting = 1;  //Log enable by default
int log_interval = 5000;  //default 5000 ms
int polling_interval = 5000; //default 5000 ms
int measured_flag = 0;
int count_equation_req = 0;

WiFiClient espClient;
PubSubClient client(espClient);

const char* host = "script.google.com";
// Link to script of google spread sheet
const char *GScriptId = "AKfycbwZagpM_hvvOnNXGWQtSiiLalvMtdFgTtrdm-segr6QVqRqxuw";
const int httpsPort = 443;
const char* fingerprint = "‎46 b2 c3 44 9c 59 09 8b 01 b6 f8 bd 4c fb 00 74 91 2f ef f6";
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
// time stamp
String tsm_year = "";
String tsm_mon = "";
String tsm_day = "";
String tsm_hour = "";
String tsm_min = "";
String tsm_sec = "";

void setup_wifi()
{ 
  int count_recon = 0;
  int count_wifi = 0;
  printf("Node %s\r\n", nodeID);
  printf("Connecting to %s\r\n", ssid);
  //WiFi.config(ip, gateway, subnet);
  if (WiFi.begin(ssid, password) != 0)
  {
        while (WiFi.status() != WL_CONNECTED)
        {
            digitalWrite(LED_BUILTIN, LOW);
            delay(50);
            Serial.print(".");
            digitalWrite(LED_BUILTIN, HIGH);
            delay(50);
            count_wifi = count_wifi + 1;
            if(count_wifi > 1000)
            {
              delay(30000);
              ESP.restart();
            }
        }
   }

  printf("WiFi connected. IP address:\r\n");
  Serial.println(WiFi.localIP());

  clientg = new HTTPSRedirect(httpsPort);
  clientg->setPrintResponseBody(true);
  clientg->setContentTypeHeader("application/json");
  printf("Connecting to %s\r\n", host);
  while( (clientg->connect(host, httpsPort)) != 1)
  {
    if(count_recon > 5)
    {
      //wait tcp time out at google script
      delay(30000);
      ESP.restart();
    }
    digitalWrite(LED_BUILTIN, LOW);
    printf(".");
    delay(256);
    digitalWrite(LED_BUILTIN, HIGH);
    delay(256);
    count_recon = count_recon + 1;
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
    char encript_payload[32];
    int i;

    for(i = 0; i < NUM_PHASE; i++)
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
        p = m_slope*f + C_const;
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

      //New version msg
      power_str[i] = String(p);
      power_str[i].toCharArray(power_payload, power_str[i].length() + 1);
      msg_topic = String(nodeID) + "_p" + String(i);
      msg_topic.toCharArray(power_topic, msg_topic.length() + 1);

      printf("Publish data topic %s\r\n", power_topic);
      client.publish(power_topic, power_payload);     
     }
     //Serial.println(pubMsg);
     //pubMsg.toCharArray(encript_payload, pubMsg.length() + 1);
     //client.publish(ENCRIPT_TOPIC, encript_payload);
}

void callback(char* topic, byte* payload, unsigned int length)
{
  String topic_str = String(topic);
  String payload_str;
  for(int i = 0; i < length; i++)
  {
    payload_str = payload_str + String((char)payload[i]);
  }
  
  if(topic_str == SW)
  {

    if((payload_str == "1") || (payload_str == "true"))
    {
      printf("##################SW= on\r\n");
      sw_status = ON;
    }
    else if((payload_str == "0") || payload_str == "false")
    {
      printf("##################SW= off\r\n");
      sw_status = OFF;
    }
  }
  else if(topic_str == SLOPE)
  {
    m_slope = payload_str.toFloat();
    printf("!!!!! m = %.2f\r\n", m_slope);
  }
  else if(topic_str == CONST)
  {
    C_const = payload_str.toFloat();
    printf("!!!!! C = %.2f\r\n", C_const);
  }
  else if(topic_str == LOG_SETTING)
  {
    log_setting = payload_str.toInt();
    if(log_setting == 1)
    {
      printf("Log enable.\r\n");
    }
    else if(log_setting == 0)
    {
      printf("Log disable.\r\n");
    }
    else
    {
      printf("ERR0x01: Unknow log status.");
    }
  }
  else if(topic_str == LOG_INTERVAL)
  {
    log_interval = payload_str.toInt();
    printf("Log interval = %d\r\n", log_interval);
  }
  else if(topic_str == POLLING_INTERVAL)
  {
    polling_interval = payload_str.toInt();
    printf("Polling interval = %d\r\n", polling_interval);
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
  String nid_str = NID; 
  int timeZone = 7*3600;

  pinMode(LED_BUILTIN, OUTPUT);
  for(i = 0; i < nid_str.toInt(); i++)
  {
    digitalWrite(LED_BUILTIN, LOW);
    delay(200);
    digitalWrite(LED_BUILTIN, HIGH);
    delay(200);
  }
  delay(500);
  #ifndef SIM_MODE
  for(i = 0; i < NUM_PHASE; i ++)
  {
    // Set pin_in to measure pulse width
    pinMode(phaseID[i].pinIn, INPUT);
    //printf("PinIn= %d\r\n", phaseID.pinIn);
  }
  #endif
  
  Serial.begin(115200);
  Serial.println("\r\nAP: 4.21");
  free_heap_before = ESP.getFreeHeap();
  free_stack_before = cont_get_free_stack(&g_cont);

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
  reconnect();
  printf("\r\n");
  
}

void reconnect()
{
  int err_reconnect = 0;
  // Loop until we’re reconnected
  while (!client.connected())
  {
    if(err_reconnect > 10)
    {
      ESP.restart();
    }
    else if(err_reconnect > 5)
    {
      setup_wifi();
    }
    printf("Attempting MQTT connection... "); 
    // Attempt to connect
    if (client.connect(client_id, mqttUser, mqttPassword))
    {
      printf("connected.");    
      client.subscribe(nodeID);
      client.subscribe(SW);
      client.subscribe(SLOPE);
      client.subscribe(CONST);
      client.subscribe(LOG_SETTING);
      client.subscribe(LOG_INTERVAL);
      client.subscribe(POLLING_INTERVAL);
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
    err_reconnect = err_reconnect + 1;
  }
}

void spreadsheet()
{
  static int error_count = 0;
  static int connect_count = 0;
  static bool flag = false;

  String tmpf = tsm_day + "/" + tsm_mon + "/" + tsm_year + "," + tsm_hour + ":" + tsm_min + ":" + tsm_sec;

  payload = payload_base + "\"" + phaseID[0].power + "," + phaseID[1].power + "," + phaseID[2].power + "," + tmpf + "\"}";

  if (!flag)
  {
    free_heap_before = ESP.getFreeHeap();
    free_stack_before = cont_get_free_stack(&g_cont);
    clientg = new HTTPSRedirect(httpsPort);
    flag = true;
    clientg->setPrintResponseBody(true);
    clientg->setContentTypeHeader("application/json");
    clientg->setTimeout(TIME_OUT);
  }

  if (clientg != nullptr)
  {
    if (!clientg->connected())
    {
      printf("Connecting to %s\r\n", host);
      while(clientg->connect(host, httpsPort) == 0)
      {
        printf(".");
        delay(10);
      } 
    }
    printf("POST data to spreadsheet\r\n");
    Serial.println(payload);
    if(clientg->POST(url2, host, payload))
    {
      connect_count++;
      printf("Log connect = %d\r\n", connect_count);
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

  if (error_count > MAX_ERR)
  {
    error_count = 0;
    printf("Halting processor...\r\n"); 
    delete clientg;
    clientg = nullptr;
    flag = false;
    printf("Reset cause post error over MAX_ERR\r\n");
  }
}

void measurement()
{
    int i;
    time_t mnow = time(nullptr);
    struct tm* p_tm = localtime(&mnow);
    printf("Time: %02d:%02d:%02d\r\n",p_tm->tm_hour, p_tm->tm_min, p_tm->tm_sec);

    tsm_year = String(p_tm->tm_year + 1900);
    tsm_mon = String(p_tm->tm_mon + 1);
    tsm_day = String(p_tm->tm_mday);
    tsm_hour = String(p_tm->tm_hour);
    tsm_min = String(p_tm->tm_min);
    tsm_sec = String(p_tm->tm_sec);

    #ifdef SIM_MODE
    if(sw_status == ON)
    {      
      for(i = 0; i < NUM_PHASE; i ++)
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
      phaseID[0].duration = pulseIn(phaseID[0].pinIn, LOW) + pulseIn(phaseID[0].pinIn, HIGH);
      phaseID[1].duration = pulseIn(phaseID[1].pinIn, LOW) + pulseIn(phaseID[1].pinIn, HIGH);
      phaseID[2].duration = pulseIn(phaseID[2].pinIn, LOW) + pulseIn(phaseID[2].pinIn, HIGH);
    }
    else if(sw_status == OFF)
    {
       printf("Restarting...\r\n");
       for(i = 0; i < MAX_ERR; i++)
       {
        digitalWrite(LED_BUILTIN, LOW);
        delay(100);
        digitalWrite(LED_BUILTIN, HIGH);
        delay(100);
       }
       ESP.restart();
    }
    
    #endif
}

void loop()
{

  if((m_slope == 0) && (C_const == 0))
  {
    printf("Waiting for m and C values...\r\n");
    reconnect();
    if(count_equation_req > MAX_EQUATION_REQ)
    {
      printf("Using default equation.\r\n");
      m_slope = DEFAULT_SLOPE;
      C_const = DEFAULT_CONST;
    }
    count_equation_req = count_equation_req + 1;
    delay(256);
  }
  
  // Check connection with mqtt status
  if (!client.connected())
  {
    reconnect();
  }
  client.loop();  
  now = millis();

  if((m_slope != 0) && (C_const != 0))
  {
    count_equation_req = 0;
    // Upload every 5 sec        
    if(abs( (now - lastMsg) > polling_interval) )
    {
      printf("Period = %d\r\n", now - lastMsg);
      lastMsg = now;
      // Check that node know the equition for calculating power, P = mf + c
      measurement();
      // turn on LED
      digitalWrite(LED_BUILTIN, LOW);
      measured_flag = 1;
      pubData();
      // turn off LED
      digitalWrite(LED_BUILTIN, HIGH);  
    }

    // Log data every log_interval
    if(abs(now - lastLog) > log_interval - 4)
    {
      measurement();
      printf("Log interval %d ms\r\n", now - lastLog);      
      lastLog = now;
      // turn on LED
      digitalWrite(LED_BUILTIN, LOW);
      if((log_setting == 1) && (measured_flag == 1))
      {
        spreadsheet();
      }
      // turn off LED
      digitalWrite(LED_BUILTIN, HIGH);
    }
  }
 
  delay(random(5));  
}


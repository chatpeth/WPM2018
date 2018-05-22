// Cr. CHATPETH KENANAN
// Contact: eechatpeth@gmail.com

#include <ESP8266WiFi.h>
#include <PubSubClient.h>
#include <time.h>
#include "HTTPSRedirect.h"
#include "DebugMacros.h"

//#define SIM_MODE        // Define, If need to simulation pulse in.
#define ON 1
#define OFF 0
#define NID "10"
#define SW "sw" NID
#define ALM "Alarm" NID
#define T_MAX 1000000
#define T_MIN 500
#define MAX_CONNECT 100000
#define CLID "fff" NID
#define NUM_PHASE 3
#define MAX_ERR 3
#define TIME_OUT 5000
#define SLOPE "m" NID
#define CONST "C" NID
#define ENCRIPT_TOPIC "fff" NID
#define LOG_SETTING "log_enable" NID
#define LOG_INTERVAL "log_interval" NID
#define POLLING_INTERVAL "polling_interval" NID
#define MAX_EQUATION_REQ 10
#define DEFAULT_SLOPE 1424
#define DEFAULT_CONST 204
#define SHEET_NAME "\"node" NID "\", \"values\": "
#define URL_BASE "{\"command\": \"appendRow\",\"sheet_name\": " SHEET_NAME
#define NUMBER_OF_SAMPLE 20
#define HIGH_POWER
#define PULSE_TIME_OUT 500000
#define DISPLAY
#define LOOP_BACK_PWM
#define PWM_OUT 0  //D3

#ifdef DISPLAY
#include <SPI.h>
#include <Wire.h>
#include <Adafruit_GFX.h>
#include <Adafruit_SSD1306.h>
#define OLED_RESET -1
Adafruit_SSD1306 OLED(OLED_RESET);
#endif

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

// Pa = GPIO13 = D7
// Pb = GPIO14 = D5
// Pc = GPIO12 = D6
structPhase phaseID[3] = { {1, 13, 0, 0}, {1, 14, 0, 0}, {1, 12, 0, 0} };

int sw_status = ON;
int count_connect = 0;
char nodeID[5] = NID;
const char* ssid = "atop802.11x";
const char* password = "atop3352";
IPAddress ip(192, 168, 0, 140); //set static ip
IPAddress gateway(192, 168, 0, 100); //set getteway
IPAddress subnet(255, 255, 255, 0);//set subnet
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
int connect_count = 0;

WiFiClient espClient;
PubSubClient client(espClient);

const char* host = "script.google.com";
// Link to script of google spread sheet
const char *GScriptId = "AKfycbwZagpM_hvvOnNXGWQtSiiLalvMtdFgTtrdm-segr6QVqRqxuw";
//const char *GScriptId_backup = "AKfycbyJW6L0XCxnaaUaIblDZWprteCtFoaRhPemmnIvFw3vR31t75co";
const int httpsPort = 443;
const char* fingerprint = "‎46 b2 c3 44 9c 59 09 8b 01 b6 f8 bd 4c fb 00 74 91 2f ef f6";
String url2 = String("/macros/s/") + GScriptId + "/exec?cal";
//String urlbackup = String("/macros/s/") + GScriptId_backup + "/exec?cal";
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
  Serial.print("MAC: ");
  Serial.println(WiFi.macAddress());
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
            if(count_wifi > 200)
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
    if(count_recon > 15)
    {
      //wait tcp time out at google script
      delay(30000);
      ESP.restart();
    }
    digitalWrite(LED_BUILTIN, LOW);
    printf("*");
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
    String log_connect_num_str;
    char power_topic[8];
    char Data[64];
    char power_payload[16];
    char encript_payload[32];
    char log_num[8];
    int i;

    for(i = 0; i < NUM_PHASE; i++)
    {
      float f = 0, p = 0;
      String almMsg;
      char alm[64];

      phaseID[i].power = p;
      printf("Phase %d: T = %d\r\n", i, phaseID[i].duration);
      
      // Check 0 < T < T_MAX to confirm power status    
      if(phaseID[i].duration > T_MIN && phaseID[i].duration < T_MAX)
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
        f = 1000000.000000/phaseID[i].duration; 
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

     log_connect_num_str = String(connect_count);
     log_connect_num_str.toCharArray(log_num, log_connect_num_str.length() + 1);
     client.publish("log_num/" NID , log_num);
     //Serial.println(pubMsg);
     //pubMsg.toCharArray(encript_payload, pubMsg.length() + 1);
     //client.publish(ENCRIPT_TOPIC, encript_payload);
}

void calculate_power()
{
  int i;
  
  for(i = 0; i < NUM_PHASE; i++)
  {
    float f = 0, p = 0;
    if(phaseID[i].duration > T_MIN && phaseID[i].duration < T_MAX)
    {
      if(phaseID[i].duration != 0)
      {
        f = 1000000.000000/phaseID[i].duration;
        p = m_slope*f + C_const;     
      }
      else
      {
        p = 0;
      }
    }
    phaseID[i].power = p;
    printf("Calculate power: f= %.2f, p= %.2f\r\n", f, phaseID[i].power);  
  }
  #ifdef DISPLAY
  String display_txt[NUM_PHASE];
  OLED.clearDisplay();
  OLED.setCursor(0,0);
  for(i = 0; i < NUM_PHASE; i++)
  {
    display_txt[i] = "P" + String(i + 1) + ": " + String(phaseID[i].power) + " W";
  }
  
  for(i = 0; i < NUM_PHASE; i++)
  {
    OLED.println(display_txt[i]);
  }
  OLED.setCursor(0, 0);
  OLED.display();
  
  #endif
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
  Serial.println("\r\nAP: 05222018");
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

  #ifdef DISPLAY
  OLED.begin(SSD1306_SWITCHCAPVCC, 0x3C);
  OLED.clearDisplay();
  OLED.setTextColor(WHITE);
  OLED.setCursor(0, 0);
  OLED.setTextSize(1);
  OLED.println("Node: " NID);
  OLED.display();
  #endif

  #ifdef LOOP_BACK_PWM
  Serial.println("PWM\r\n");
  pinMode(PWM_OUT, OUTPUT);
  analogWriteFreq(500);
  #endif
  
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
    int null_err = 0;
    if (!clientg->connected())
    {
      printf("DB0xff:Reconnecting to %s\r\n", host);
      
      while( (clientg->connect(host, httpsPort)) != 1)
      {
        printf("**");
        if(null_err > 3)
        {
          ESP.restart();
        }
        null_err = null_err + 1;
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
    unsigned long duration1 = 0, duration2 = 0, duration3 = 0;

    
    #ifdef LOOP_BACK_PWM
    int ranf = random(1000);
    if(ranf == 0)
    {
      ranf = 1;
    }
    printf("rand_f= %d\r\n", ranf);  
    analogWriteFreq(ranf);
    analogWrite(PWM_OUT, 512);
    #endif
    
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
      #ifdef HIGH_POWER
      printf("Hight power mode\r\n");
      unsigned long tmp_duration1 = 0, tmp_duration2 = 0, tmp_duration3 = 0;
      tmp_duration1 = pulseIn(phaseID[0].pinIn, LOW, PULSE_TIME_OUT);
      tmp_duration2 = pulseIn(phaseID[1].pinIn, LOW, PULSE_TIME_OUT);
      tmp_duration3 = pulseIn(phaseID[2].pinIn, LOW, PULSE_TIME_OUT);
      //1000000 us = 1 Hz
      //10000 us = 100 Hz ~ 6000 W
      printf("tmp duration = %d, %d, %d\r\n", tmp_duration1, tmp_duration2, tmp_duration3);
      if((tmp_duration1 != 0) && (tmp_duration1 < 2*10000))
      {
        printf("PhaseA hight frequency\r\n");
        for(i = 0; i < NUMBER_OF_SAMPLE; i++)
        {          
          duration1 = duration1 + pulseIn(phaseID[0].pinIn, LOW, PULSE_TIME_OUT) + pulseIn(phaseID[0].pinIn, HIGH, PULSE_TIME_OUT);          
        }
        phaseID[0].duration = duration1/NUMBER_OF_SAMPLE;       
      }
      else
      {
        printf("PhaseA low frequency\r\n");
        if(tmp_duration1 != 0)
        {
          phaseID[0].duration = pulseIn(phaseID[0].pinIn, LOW, PULSE_TIME_OUT) + pulseIn(phaseID[0].pinIn, HIGH, PULSE_TIME_OUT);
        }
        else
        {
          phaseID[0].duration = 0;
        }
        
      }

      if((tmp_duration2 != 0) && (tmp_duration2 < 2*10000))
      {
        printf("PhaseB hight frequency\r\n");
        for(i = 0; i < NUMBER_OF_SAMPLE; i++)
        {         
          duration2 = duration2 + pulseIn(phaseID[1].pinIn, LOW, PULSE_TIME_OUT) + pulseIn(phaseID[1].pinIn, HIGH, PULSE_TIME_OUT);          
        }       
        phaseID[1].duration = duration2/NUMBER_OF_SAMPLE;        
      }
      else
      {
        printf("PhaseB low frequency\r\n");
        if(tmp_duration2 != 0)
        {
          phaseID[1].duration = pulseIn(phaseID[1].pinIn, LOW, PULSE_TIME_OUT) + pulseIn(phaseID[1].pinIn, HIGH, PULSE_TIME_OUT);
        }
        else
        {
          phaseID[1].duration = 0;
        }
        
      }

      if((tmp_duration3 != 0) && (tmp_duration3 < 2*10000))
      {
        printf("PhaseC hight frequency\r\n");
        for(i = 0; i < NUMBER_OF_SAMPLE; i++)
        {         
          duration3 = duration3 + pulseIn(phaseID[2].pinIn, LOW, PULSE_TIME_OUT) + pulseIn(phaseID[2].pinIn, HIGH, PULSE_TIME_OUT);         
        }       
        phaseID[2].duration = duration3/NUMBER_OF_SAMPLE;        
      }
      else
      {
        printf("PhaseC low frequency\r\n");
        if(tmp_duration3 != 0)
        {
          phaseID[2].duration = pulseIn(phaseID[2].pinIn, LOW, PULSE_TIME_OUT) + pulseIn(phaseID[2].pinIn, HIGH, PULSE_TIME_OUT);
        }
        else
        {
          phaseID[2].duration = 0;
        }
        
      }
      
            
      #else
      // 1 Sample
      unsigned long low_tmp_duration[3];
      int i;
      for(i = 0; i < 3; i++)
      {
        low_tmp_duration[i] = pulseIn(phaseID[i].pinIn, LOW, PULSE_TIME_OUT);
      }
      
      for(i = 0; i < 3; i ++)
      {
        if(low_tmp_duration[i] == 0)
        {
          // P = 0 W
           phaseID[i].duration = 0;
        }
        else
        {
          phaseID[i].duration = low_tmp_duration[i] + pulseIn(phaseID[i].pinIn, HIGH, PULSE_TIME_OUT);
        }
      }
      
     

      #endif
      
      
      
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

    calculate_power();
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
    delay(1000);
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
          
    if(abs( (now - lastMsg) > (polling_interval - 64)) )
    {
      printf("Period = %d\r\n", now - lastMsg);
      lastMsg = now;
      // Check that node know the equition for calculating power, P = mf + c    
      // turn on LED
      digitalWrite(LED_BUILTIN, LOW);
      measured_flag = 1;
      pubData();
      // turn off LED
      digitalWrite(LED_BUILTIN, HIGH);  
    }

    // Log data every log_interval
    if(abs(now - lastLog) > (log_interval - 64))
    {
      
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

    if(abs (now - lastMsg) > 1000)
    {
      measurement();
    }
  }
 
  delay(random(5));  
}

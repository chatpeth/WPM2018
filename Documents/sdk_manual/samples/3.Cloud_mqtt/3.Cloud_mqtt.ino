// Measuring frequency and sent to Raspberrypi
// Use mqtt protocol
// Cr. Chatpeth


#include <ESP8266WiFi.h>
#include <PubSubClient.h>

#define SIM_MODE        // Define, If need to simulation pulse in.
#define debug_Mode 1    // Define, If need to open serial port.

typedef struct
{
  int state;
  int pinIn;
  unsigned long duration;
}structPhase;
// State = 0 if power is down, Other state = 1

// Phase A = GPIO2 = D4
// Phase B = GPIO4 = D2
// Phase C = GPIO5 = D1
structPhase phaseID[3] = { {1, 2, 0}, {1, 4, 0}, {1, 5, 0} };

int pout = 13;
char nodeID[16] = "mywpm2018";
const char* ssid = "atop802.11x";
const char* password = "atop3352";
const char* mqttServer = "m13.cloudmqtt.com";
const int mqttPort = 10800;
const char* mqttUser = "axkcmxbz";
const char* mqttPassword = "CiNyQHm1jOTi";
const char* client_id = nodeID;

unsigned long now;
long lastMsg = 0;

WiFiClient espClient;
PubSubClient client(espClient);

void setup_wifi()
{
  delay(10);
  
  // We start by connecting to a WiFi network
  #ifdef debug_Mode
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(ssid);
  #endif
  
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED)
  {
    delay(500);
    Serial.print(".");
  }
  
  #ifdef debug_Mode
  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());
  #endif
}

void pubData()
{
    String ldrv;    
    String pubMsg;
    char Data[64];
    int i;
    float f = 0;
        
    for(i = 0; i < 3 ; i++)
    {
      float f = 0;
      String almMsg;
      char alm[64];
      #ifdef debug_Mode      
      Serial.print("Phase ");
      Serial.print(i);
      Serial.print(" --> D = ");
      Serial.println(phaseID[i].duration);
      #endif
      // Check 0 < T/2 < 500000    
      if(phaseID[i].duration != 0 && phaseID[i].duration < 500000)
      {
        // Power is back 
        if(phaseID[i].state == 0)
        {
          almMsg = "Node " + String(nodeID) + " Phase " + String(i) + " is up.\n";          
          almMsg.toCharArray(alm, almMsg.length() + 1);
          #ifdef debug_Mode 
          Serial.print(almMsg);
          #endif
          client.publish("Alarm", alm);
          // Clear blackout flag
          phaseID[i].state = 1;
        }
        
        f = 500000/phaseID[i].duration;  //approximate frequency 
      }
      // Blackout
      else if(phaseID[i].duration == 0 || phaseID[i].duration > 500000)
      {
        // Approximate that P ~ 0
        phaseID[i].duration = 0;
        if(phaseID[i].state == 1)
        {
          almMsg = "Node " + String(nodeID) + " Phase " + String(i) + " is down.\n";      
          almMsg.toCharArray(alm, almMsg.length() + 1);
          client.publish("Alarm", alm);
          #ifdef debug_Mode 
          Serial.print(almMsg);
          #endif
          // Set blackout flag
          phaseID[i].state = 0;                
        }
      } 
           
      #ifdef debug_Mode       
      Serial.print("f = " + String(f) + "\n");
      #endif
      // phase duration = T/2
      ldrv = String(phaseID[i].duration);
      // message to public to mqtt brocker
      pubMsg = pubMsg +  ldrv + "\n";
     }

    // Convert to array of char 
    pubMsg.toCharArray(Data, pubMsg.length() + 1);
     
    #ifdef debug_Mode
    Serial.print("Publish message: ");
    Serial.println(pubMsg);
    Serial.println("-----");
    #endif 
    // Public data of node ID xxxx
    client.publish(nodeID, Data);    
}

void callback(char* topic, byte* payload, unsigned int length)
{
  #ifdef debug_Mode
  Serial.print("Message arrived [");
  Serial.print(topic);
  Serial.print("] ");
  Serial.print("came into callback. The published message from Pi is ");
  Serial.println();
  
  for (int i = 0; i < length; i++)
  {
    Serial.print((char)payload[i]);
  }
  Serial.println();
  #endif
}

void setup()
{
  int i;
  #ifndef SIM_MODE
  for(i = 0; i < 3; i ++)
  {
    // Set pin_in to measure pulse width
    pinMode(phaseID[i].pinIn, INPUT);
    Serial.print("PinIn = ");
    Serial.print(phaseID[i].pinIn);
  }
  #endif
  
  #ifdef debug_Mode
  Serial.begin(115200);
  #endif

  setup_wifi();
  client.setServer(mqttServer, mqttPort);
  client.setCallback(callback);
  
  pinMode(pout, OUTPUT);
}

void reconnect()
{
  // Loop until we’re reconnected
  while (!client.connected())
  {
    #ifdef debug_Mode
    Serial.print("Attempting MQTT connection... ");
    #endif
    
    // Attempt to connect
    if (client.connect(client_id, mqttUser, mqttPassword))
    {
      
      #ifdef debug_Mode
      Serial.println("connected");
      #endif
     
      
      // … and resubscribe
      client.subscribe(nodeID);
    }
    else
    {
      #ifdef debug_Mode
      Serial.print("failed, rc= ");
      Serial.print(client.state());
      Serial.println(" ...try again in no longer");
      #endif
      // Wait 0 - 5 seconds before retrying
      delay(random(5000));
    }
  }
}

void loop()
{
  int i;    
  // Check connection status
  if (!client.connected() )
  {
    reconnect();
  }
  client.loop();  
  
  now = millis();
  

  // Upload everry 1 min        
  if(abs( (now - lastMsg) > 50000) )
  {
    lastMsg = now;

     #ifdef SIM_MODE
    int i;
    for(i = 0; i < 3; i ++)
    {
      phaseID[i].duration = random(5000, 50000);
    }
    #else
    phaseID[0].duration = pulseIn(phaseID[0].pinIn, LOW);
    phaseID[1].duration = pulseIn(phaseID[1].pinIn, LOW);
    phaseID[2].duration = pulseIn(phaseID[2].pinIn, LOW);
    #endif
  
    // turn on LED
    digitalWrite(pout, HIGH);
    pubData();
    // turn off LED
    digitalWrite(pout, LOW);

    for(i = 0; i < 3; i++)
    {
      phaseID[2].duration = 0; 
    }
  }  
    
  delay(random(5000));  
}


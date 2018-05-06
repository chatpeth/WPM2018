// Pa = GPIO5 =D1
// Pb = GPIO4 = D2
// Pc = GPIO14 = D5

#define PA 5
#define PB 4
#define PC 14


void setup() { 

  Serial.begin(115200);
  Serial.println("\r\n PWM");
  pinMode(PA, OUTPUT);
  pinMode(PB, OUTPUT);
  pinMode(PC, OUTPUT);
  analogWriteFreq(500);

}

void loop() {

  int ranf = random(1000);
  if(ranf == 0)
  {
    ranf = 1;
  }
  printf("f=%d, p=%d\r\n", ranf, ranf*60 -2);
  analogWriteFreq(ranf);
  analogWrite(PA, 512);
  analogWrite(PB, 512);
  analogWrite(PC, 512);
  delay(60000);


}

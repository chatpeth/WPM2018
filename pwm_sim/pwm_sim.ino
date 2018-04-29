#define PWM_OUT1 5 //D1
#define PWM_OUT2 4 //D2
#define PWM_OUT3 14 //D5
#define PWM_PIN 16


void setup() { 
  //pinMode(PWM_OUT1, OUTPUT);
  pinMode(PWM_PIN, OUTPUT);

}

void loop() {
  int D = 500;
  analogWrite(PWM_PIN, 512);
  //digitalWrite(PWM_OUT1, HIGH);
  //delayMicroseconds(D);
  //digitalWrite(PWM_OUT1, LOW);
  //delayMicroseconds(D);

}

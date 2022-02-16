const int CONTROLLED_OUT = 2;
byte parseState = 0;
byte demandedState = 0; 

void setup() {
  pinMode(LED_BUILTIN, OUTPUT); 
  pinMode(CONTROLLED_OUT, OUTPUT);
  Serial.begin(9600);
}

void setOutputState(byte demandedState) {
  if (demandedState == 1) {
    digitalWrite(CONTROLLED_OUT, HIGH);
    digitalWrite(LED_BUILTIN, HIGH);
  }
  else {
    digitalWrite(CONTROLLED_OUT, LOW);
    digitalWrite(LED_BUILTIN, LOW);
  }
}

void loop() {
  if (Serial.available()) {
    char c = Serial.read();
    switch(parseState) {
      case 0: 
        if (c == '>') parseState = 1; 
        break; 
      case 1: 
        if (c == '1') {
          demandedState = 1;
          parseState = 2; 
        } else if (c == '0') {
          demandedState = 0; 
          parseState = 2; 
        } else parseState = 0; 
        break; 
      
      case 2: 
        if (c == '\n') {
          parseState = 0; 
          setOutputState(demandedState); 
        }
        
    }
  }
}

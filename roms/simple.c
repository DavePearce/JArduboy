#include <avr/io.h>
#include <util/delay.h>
#include <math.h>

int display[16][8];

void display_write(int c) {
  for(int i=0;i<8;++i) {
    PORTB = 0b00000000;
    if((c & 1) == 1) {
      PORTB = 0b00000011;
    } else {
      PORTB = 0b00000001;
    }
    c = c >> 1;
  }
}

void fill(int value) {
  for(int i=0;i!=8;++i) {
    for(int j=0;j!=8;++j) {
      display[i][j] = value;
    }  
  }
}

void refresh() {
  for(int i=0;i<8;++i) {
    for(int k=0;k<8;++k) {
      for(int j=0;j<8;++j) {
	display_write(display[j][i]);	
      }
    }
  }
}

// =========================================================
// Setup
// =========================================================

void setup() {
  // set SCLK, MOSI, MISO, SS to be output
  //DDRB = 0b00001111;
  //PORTB = 0b00000000;
}

void main() {
  setup();
  //
  //fill(0xff);
  //
  //refresh();
}

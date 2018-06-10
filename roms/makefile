# Makefile for programming the ATtiny85 via micronucleous USB
# bootloader
DEVICE      = atmega32u4
CLOCK      = 16000000UL
PROGRAMMER = avr109
PORT = /dev/cu.usbmodem1411
BAUD = 57600
#AVRDUDE = avrdude -v -p$(DEVICE) -c$(PROGRAMMER) -P$(PORT) -b$(BAUD) -D
AVRDUDE = /Applications/Arduino.app/Contents/Java/hardware/tools/avr/bin/avrdude -C /Applications/Arduino.app/Contents/Java/hardware/tools/avr/etc/avrdude.conf -v -p$(DEVICE) -c$(PROGRAMMER) -P$(PORT) -b$(BAUD) -D -V
COMPILE = avr-gcc -fstack-usage -Wall -Os -std=c99 -DF_CPU=$(CLOCK) -mmcu=$(DEVICE)

TARGETS = simple.hex

all: $(TARGETS)

%.s : %.c
	$(COMPILE) -S -o $@ $<

%.o : %.c
	$(COMPILE) -c -o $@ $<

%.elf: %.o
	$(COMPILE) -o $@ $< 

%.hex: %.elf
	avr-objcopy -j .text -j .data -O ihex $< $@
	avr-size --format=avr --mcu=$(DEVICE) $<

%.upload: %.hex
	python reset.py $(PORT)
	sleep 2
	$(AVRDUDE) -Uflash:w:./$<:i 

clean:
	rm -f *.hex *.elf *.o

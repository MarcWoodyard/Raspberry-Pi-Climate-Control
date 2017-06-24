# Raspberry Pi Climate Control

A simple Java program to turn on an off a old push button AC unit when the room temperature reaches a certain temperature.

<p align="center">
  <img width="460" height="300" src="https://raw.githubusercontent.com/MarcWoodyard/Raspberry-Pi-Climate-Control/master/screenshot.png">
</p>

### Dependencies

- Java
- Raspberry Pi
- [Pi4J](http://pi4j.com/install.html) - Java I/O library for the Raspberry Pi

### Troubleshooting

> Unable to determine hardware version. I see: Hardware: BCM2835

https://github.com/Pi4J/pi4j/issues/319 

**Solution:** 
```sh 
sudo rpi-update 52241088c1da59a359110d39c1875cda56496764
```

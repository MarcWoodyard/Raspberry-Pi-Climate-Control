package com.marcwoodyard.RaspberryPiThermostat.web.controllers;

import com.marcwoodyard.RaspberryPiThermostat.Launcher;
import com.marcwoodyard.RaspberryPiThermostat.peripherals.DHT11;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @RequestMapping(value = {"", "/", "thermostat"})
    public String listCameras(Model model) {
        model.addAttribute("temperature", DHT11.getTemperature() + "");
        model.addAttribute("humidity", DHT11.getHumidity());
        model.addAttribute("time", Launcher.getMinutesRunning());

        if (Launcher.getACOn())
            model.addAttribute("mode", "Cooling");
        else
            model.addAttribute("mode", "Monitoring");

        return "home";
    }

}

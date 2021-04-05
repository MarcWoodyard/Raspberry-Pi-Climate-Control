package com.marcwoodyard.RaspberryPiThermostat.web.controllers;

import com.marcwoodyard.RaspberryPiThermostat.Launcher;
import com.marcwoodyard.RaspberryPiThermostat.peripherals.DHT11;
import com.marcwoodyard.RaspberryPiThermostat.peripherals.RaspiStill;
import com.marcwoodyard.RaspberryPiThermostat.peripherals.RelaySwitch;
import com.marcwoodyard.RaspberryPiThermostat.utils.Logger;
import com.marcwoodyard.RaspberryPiThermostat.utils.ProgramSettings;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String listCameras(Model model) {
        model.addAttribute("temperature", DHT11.getTemperature() + "");
        model.addAttribute("humidity", DHT11.getHumidity());
        model.addAttribute("time", Launcher.getMinutesRunning());

        model.addAttribute("programSettings", new ProgramSettings());
        model.addAttribute("logData", Logger.getLogs());
        //model.addAttribute("acStatus", RelaySwitch.isAcStatus());

        if (Launcher.getACOn())
            model.addAttribute("mode", "Cooling");
        else
            model.addAttribute("mode", "Monitoring");

        return "dashboard";
    }

    @PostMapping(value = "/dashboard", params = "programSettings")
    public String submitSettingsForm(@ModelAttribute("programSettings") ProgramSettings programSettings, BindingResult result) {
        if (result.hasErrors())
            return "redirect:/settings#error";

        return "redirect:/settings#success";
    }

    @RequestMapping("/api/toggle-on-off")
    public String toggleOnOff(@ModelAttribute("programSettings") ProgramSettings programSettings, BindingResult result) {
        RelaySwitch.toggleAC();

        if (result.hasErrors())
            return "redirect:/settings#error";

        return "redirect:/settings#success";
    }

    @RequestMapping("/api/filter-reset")
    public String filterReset(@ModelAttribute("programSettings") ProgramSettings programSettings, BindingResult result) {
        RelaySwitch.filterReset();

        if (result.hasErrors())
            return "redirect:/settings#error";

        return "redirect:/settings#success";
    }

    @RequestMapping(value = "/api/view-screenshot", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getImgAsBytes(final HttpServletResponse response) {
        try {
            String picName = "live.png";
            FileInputStream in = new FileInputStream(picName);
            RaspiStill.takePicture(picName, "png");
            return new ResponseEntity<>(IOUtils.toByteArray(in), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

}

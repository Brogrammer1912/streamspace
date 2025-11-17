package com.brogrammer.streamspace.preferences;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/preference")
@RequiredArgsConstructor
public class PreferencesAPI {

    final UserPreferences userPreferences;

    @PatchMapping
    public ResponseEntity<Void> saveDarkModePreference(@RequestBody Preference preference) {
        Optional<Preference> existingPref = userPreferences.findById(preference.getPrefId());
        if (existingPref.isPresent()) {
            Preference updatedPref = existingPref.get();
            updatedPref.setEnabled(preference.isEnabled());
            userPreferences.save(updatedPref);
        } else {
            //userPreferences.save(preference);
            log.info("Preference with id {} not found", preference.getPrefId());
        }
        return ResponseEntity.ok().build();
    }
}

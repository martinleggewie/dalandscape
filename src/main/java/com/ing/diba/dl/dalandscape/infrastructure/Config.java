package com.ing.diba.dl.dalandscape.infrastructure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "dalandscape")
public class Config {
  private String customSetting;
  private String outputPath;

  public String getOutputPath() {
    return outputPath;
  }

  public void setOutputPath(String outputPath) {
    this.outputPath = outputPath;
  }

  public String getCustomSetting() {
    return customSetting;
  }

  public void setCustomSetting(String customSetting) {
    this.customSetting = customSetting;
  }
}

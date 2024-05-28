package com.roaaserver.placementstudent.Models;

public class AppVersionClass {
   float versionCode;
   String versionName;
   String updateLink;

   AppVersionClass(){

   }

   public AppVersionClass(int versionCode, String versionName, String updateLink) {
      this.versionCode = versionCode;
      this.versionName = versionName;
      this.updateLink = updateLink;
   }

   public float getVersionCode() {
      return versionCode;
   }

   public void setVersionCode(float versionCode) {
      this.versionCode = versionCode;
   }

   public String getVersionName() {
      return versionName;
   }

   public void setVersionName(String versionName) {
      this.versionName = versionName;
   }

   public String getUpdateLink() {
      return updateLink;
   }

   public void setUpdateLink(String updateLink) {
      this.updateLink = updateLink;
   }
}

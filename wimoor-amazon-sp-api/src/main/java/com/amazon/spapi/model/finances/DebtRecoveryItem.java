/*
 * Selling Partner API for Finances
 * The Selling Partner API for Finances helps you obtain financial information relevant to a seller's business. You can obtain financial events for a given order, financial event group, or date range without having to wait until a statement period closes. You can also obtain financial event groups for a given date range.
 *
 * OpenAPI spec version: v0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package com.amazon.spapi.model.finances;

import java.util.Objects;
import java.util.Arrays;
import com.amazon.spapi.model.finances.Currency;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.IOException;

/**
 * An item of a debt payment or debt adjustment.
 */
@ApiModel(description = "An item of a debt payment or debt adjustment.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-12-15T20:01:58.583+08:00")
public class DebtRecoveryItem {
  @SerializedName("RecoveryAmount")
  private Currency recoveryAmount = null;

  @SerializedName("OriginalAmount")
  private Currency originalAmount = null;

  @SerializedName("GroupBeginDate")
  private String groupBeginDate = null;

  @SerializedName("GroupEndDate")
  private String groupEndDate = null;

  public DebtRecoveryItem recoveryAmount(Currency recoveryAmount) {
    this.recoveryAmount = recoveryAmount;
    return this;
  }

   /**
   * The amount applied for the recovery item.
   * @return recoveryAmount
  **/
  @ApiModelProperty(value = "The amount applied for the recovery item.")
  public Currency getRecoveryAmount() {
    return recoveryAmount;
  }

  public void setRecoveryAmount(Currency recoveryAmount) {
    this.recoveryAmount = recoveryAmount;
  }

  public DebtRecoveryItem originalAmount(Currency originalAmount) {
    this.originalAmount = originalAmount;
    return this;
  }

   /**
   * The original debt amount.
   * @return originalAmount
  **/
  @ApiModelProperty(value = "The original debt amount.")
  public Currency getOriginalAmount() {
    return originalAmount;
  }

  public void setOriginalAmount(Currency originalAmount) {
    this.originalAmount = originalAmount;
  }

  public DebtRecoveryItem groupBeginDate(String groupBeginDate) {
    this.groupBeginDate = groupBeginDate;
    return this;
  }

   /**
   * The beginning date and time of the financial event group that contains the debt. In ISO 8601 date time format.
   * @return groupBeginDate
  **/
  @ApiModelProperty(value = "The beginning date and time of the financial event group that contains the debt. In ISO 8601 date time format.")
  public String getGroupBeginDate() {
    return groupBeginDate;
  }

  public void setGroupBeginDate(String groupBeginDate) {
    this.groupBeginDate = groupBeginDate;
  }

  public DebtRecoveryItem groupEndDate(String groupEndDate) {
    this.groupEndDate = groupEndDate;
    return this;
  }

   /**
   * The ending date and time of the financial event group that contains the debt. In ISO 8601 date time format.
   * @return groupEndDate
  **/
  @ApiModelProperty(value = "The ending date and time of the financial event group that contains the debt. In ISO 8601 date time format.")
  public String getGroupEndDate() {
    return groupEndDate;
  }

  public void setGroupEndDate(String groupEndDate) {
    this.groupEndDate = groupEndDate;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DebtRecoveryItem debtRecoveryItem = (DebtRecoveryItem) o;
    return Objects.equals(this.recoveryAmount, debtRecoveryItem.recoveryAmount) &&
        Objects.equals(this.originalAmount, debtRecoveryItem.originalAmount) &&
        Objects.equals(this.groupBeginDate, debtRecoveryItem.groupBeginDate) &&
        Objects.equals(this.groupEndDate, debtRecoveryItem.groupEndDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(recoveryAmount, originalAmount, groupBeginDate, groupEndDate);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DebtRecoveryItem {\n");
    
    sb.append("    recoveryAmount: ").append(toIndentedString(recoveryAmount)).append("\n");
    sb.append("    originalAmount: ").append(toIndentedString(originalAmount)).append("\n");
    sb.append("    groupBeginDate: ").append(toIndentedString(groupBeginDate)).append("\n");
    sb.append("    groupEndDate: ").append(toIndentedString(groupEndDate)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}


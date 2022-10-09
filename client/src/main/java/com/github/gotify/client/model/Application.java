/*
 * Gotify REST-API.
 * This is the documentation of the Gotify REST-API.  # Authentication In Gotify there are two token types: __clientToken__: a client is something that receives message and manages stuff like creating new tokens or delete messages. (f.ex this token should be used for an android app) __appToken__: an application is something that sends messages (f.ex. this token should be used for a shell script)  The token can be either transmitted through a header named `X-Gotify-Key` or a query parameter named `token`. There is also the possibility to authenticate through basic auth, this should only be used for creating a clientToken.  \\---  Found a bug or have some questions? [Create an issue on GitHub](https://github.com/gotify/server/issues)
 *
 * OpenAPI spec version: 2.0.1
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package com.github.gotify.client.model;

import java.util.Objects;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.IOException;

/**
 * The Application holds information about an app which can send notifications.
 */
@ApiModel(description = "The Application holds information about an app which can send notifications.")
public class Application {
  @SerializedName("description")
  private String description = null;

  @SerializedName("id")
  private Long id = null;

  @SerializedName("image")
  private String image = null;

  @SerializedName("internal")
  private Boolean internal = null;

  @SerializedName("name")
  private String name = null;

  @SerializedName("token")
  private String token = null;

  public Application description(String description) {
    this.description = description;
    return this;
  }

   /**
   * The description of the application.
   * @return description
  **/
  @ApiModelProperty(example = "Backup server for the interwebs", required = true, value = "The description of the application.")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

   /**
   * The application id.
   * @return id
  **/
  @ApiModelProperty(example = "5", required = true, value = "The application id.")
  public Long getId() {
    return id;
  }

   /**
   * The image of the application.
   * @return image
  **/
  @ApiModelProperty(example = "image/image.jpeg", required = true, value = "The image of the application.")
  public String getImage() {
    return image;
  }

   /**
   * Whether the application is an internal application. Internal applications should not be deleted.
   * @return internal
  **/
  @ApiModelProperty(example = "false", required = true, value = "Whether the application is an internal application. Internal applications should not be deleted.")
  public Boolean isInternal() {
    return internal;
  }

  public Application name(String name) {
    this.name = name;
    return this;
  }

   /**
   * The application name. This is how the application should be displayed to the user.
   * @return name
  **/
  @ApiModelProperty(example = "Backup Server", required = true, value = "The application name. This is how the application should be displayed to the user.")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

   /**
   * The application token. Can be used as &#x60;appToken&#x60;. See Authentication.
   * @return token
  **/
  @ApiModelProperty(example = "AWH0wZ5r0Mbac.r", required = true, value = "The application token. Can be used as `appToken`. See Authentication.")
  public String getToken() {
    return token;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Application application = (Application) o;
    return Objects.equals(this.description, application.description) &&
        Objects.equals(this.id, application.id) &&
        Objects.equals(this.image, application.image) &&
        Objects.equals(this.internal, application.internal) &&
        Objects.equals(this.name, application.name) &&
        Objects.equals(this.token, application.token);
  }

  @Override
  public int hashCode() {
    return Objects.hash(description, id, image, internal, name, token);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Application {\n");
    
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    image: ").append(toIndentedString(image)).append("\n");
    sb.append("    internal: ").append(toIndentedString(internal)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    token: ").append(toIndentedString(token)).append("\n");
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


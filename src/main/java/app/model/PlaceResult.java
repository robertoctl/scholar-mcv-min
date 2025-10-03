package app.model;


import lombok.Builder;

@Builder
public class PlaceResult
{
    public Integer position;
    public String title;
    public String type;
    public String address;
    public Double rating;
    public Integer reviews;
    public String price;
    public String description;
    public String thumbnail;
    public String placeId;
    public Double latitude;
    public Double longitude;
    public String hours;
}


package io.farmx.dto;

public class FarmDTO {
    private Long id;
    private String name;
    private String location;
    private double areaSize;

    // 
    public FarmDTO() {}

    public FarmDTO(Long id, String name, String location, double areaSize) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.areaSize = areaSize;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public double getAreaSize() { return areaSize; }
    public void setAreaSize(double areaSize) { this.areaSize = areaSize; }
}

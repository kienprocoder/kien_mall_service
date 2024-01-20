package vn.restapi.kienmall.common;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PageRequest {
    private int page;
    private int size;

}

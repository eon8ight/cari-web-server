package com.cari.web.server.dto.request;

import java.io.Serializable;
import org.springframework.web.multipart.MultipartFile;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EntityEditRequest implements Serializable {

    private static final long serialVersionUID = -5072738279222505834L;

    private String username;

    private String emailAddress;

    private String password;

    private String firstName;

    private String lastName;

    private String biography;

    private String title;

    private MultipartFile profileImage;

    private Integer favoriteAesthetic;
}

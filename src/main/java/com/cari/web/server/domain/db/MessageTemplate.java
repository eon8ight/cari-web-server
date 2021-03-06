package com.cari.web.server.domain.db;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table("tb_message_template")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageTemplate implements Serializable {

    private static final long serialVersionUID = 1834636000891676566L;

    public static final int CONFIRM_ACCOUNT = 1;
    public static final int RESET_PASSWORD = 2;
    public static final int INVITE = 3;

    private static final String COLUMN_MESSAGE_TEMPLATE = "message_template";
    private static final String COLUMN_EXT_ID = "ext_id";

    @Id
    @Column(COLUMN_MESSAGE_TEMPLATE)
    @JsonAlias(COLUMN_MESSAGE_TEMPLATE)
    private Integer messageTemplate;

    @Column(COLUMN_EXT_ID)
    @JsonAlias(COLUMN_EXT_ID)
    private String extId;
}

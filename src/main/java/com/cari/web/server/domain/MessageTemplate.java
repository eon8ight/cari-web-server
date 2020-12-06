package com.cari.web.server.domain;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonAlias;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("tb_message_template")
public class MessageTemplate implements Serializable {

    private static final long serialVersionUID = 1834636000891676566L;

    public static final int CONFIRM_ACCOUNT = 1;

    private static final String COLUMN_MESSAGE_TEMPLATE = "message_template";
    private static final String COLUMN_BODY_PLAINTEXT = "body_plaintext";
    private static final String COLUMN_BODY_HTML = "body_html";
    private static final String COLUMN_EXT_ID = "ext_id";

    @Id
    @Column(COLUMN_MESSAGE_TEMPLATE)
    @JsonAlias({COLUMN_MESSAGE_TEMPLATE})
    private int messageTemplate;

    @NotNull
    private String subject;

    @NotNull
    @Column(COLUMN_BODY_PLAINTEXT)
    @JsonAlias({COLUMN_BODY_PLAINTEXT})
    private String bodyPlaintext;

    @NotNull
    @Column(COLUMN_BODY_HTML)
    @JsonAlias({COLUMN_BODY_HTML})
    private String bodyHtml;

    @NotNull
    @Column(COLUMN_EXT_ID)
    @JsonAlias({COLUMN_EXT_ID})
    private String extId;
}

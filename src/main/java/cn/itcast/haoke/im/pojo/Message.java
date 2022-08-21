package cn.itcast.haoke.im.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "message")
@Builder
public class Message {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private String msg;
    private Integer status;
    private Date sendDate;
    private Date readDate;
    private User from;
    private User to;
}

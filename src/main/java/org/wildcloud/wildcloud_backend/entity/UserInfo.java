package org.wildcloud.wildcloud_backend.entity;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@Data
@Table("user_info")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {

    @Id
    private Long id;

    @Email
    @NotNull
    @Column("user_email")
    private String userEmail;

    @NotNull
    @Column("password")
    private String password;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private Long phoneNumber;



    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber=" + phoneNumber +
                ", email='" + userEmail + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}

package com.numble.numbledanggeun.domain.member;

import com.numble.numbledanggeun.domain.BaseEntity;
import com.numble.numbledanggeun.domain.heart.Heart;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = {"heartList"})
@Entity
public class Member extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String phone;

    private String folderPath;

    private String filename;

    @Builder.Default
    @OneToMany(mappedBy = "member",fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Heart> heartList = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(joinColumns = @JoinColumn(name = "member_id"))
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Set<MemberRole> roleSet = new HashSet<MemberRole>(Arrays.asList(MemberRole.USER));

    public void changeNickname(String nickname){
        this.nickname = nickname;
    }

    public void changeFolderPath(String folderPath){
        this.folderPath = folderPath;
    }

    public void changeFilename(String filename){
        this.filename = filename;
    }
}

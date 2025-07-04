package com.younggalee.security2.service;

import com.younggalee.security2.dto.UserDto;
import com.younggalee.security2.dto.auth.LoginUser;
import com.younggalee.security2.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;
    /**
     * 로그인(인증) 처리시 자동으로 실행되는 메소드
     *
     * @param username - 폼 로그인 요청시 전달되는 사용자명(아이디)
     * @return         - 조회된 사용자의 정보 중 인증에 필요한 최소한의 정보가 담겨있는 UserDetails객체 반환
     * @throws UsernameNotFoundException - 존재하는 사용자가 없을 경우 발생시킬 예외
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDto foundedUser = userMapper.selectUserById(username);

        if(foundedUser == null) {
            throw new UsernameNotFoundException("해당 회원 정보가 조회되지 않습니다.");
        }

        // 조회된 회원이 있을 경우
        // 해당 회원의 최소한(세션에 담을거니까)의 정보(사용자명, 비밀번호, 권한목록)를 담은 UserDetails객체를 생성해서 반환
        // 권한목록 세팅
        List<GrantedAuthority> authorityList = Arrays.stream(foundedUser.getUserRole().split(","))
                .map(role -> new SimpleGrantedAuthority(role))
                .collect(Collectors.toList());

        return new LoginUser(foundedUser.getUserId(), foundedUser.getUserPwd(), authorityList, foundedUser.getUserName());
        // User클래스가 이미 UserDetails 인터페이스로 구현된 클래스라고 함. 따라서 가능. 상속받아 필요한 것만 오버라이딩
    }
}

package jpabook.jpashop.service;

import jpabook.jpashop.Repository.MemberRepository;
import jpabook.jpashop.domain.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;

    @Test
    void 회원가입(){
        Member member = new Member();
        member.setName("강민승");
        memberService.join(member);

        Member findMember = memberService.findOne(member.getId());

        Assertions.assertThat(member).isEqualTo(findMember);
    }
    @Test
    void 중복_회원_예외(){
        Member member1 = new Member();
        Member member2 = new Member();

        member1.setName("강민승");
        member2.setName("강민승");
        memberService.join(member1);

        Assertions.assertThatThrownBy(()->memberService.join(member2)).isInstanceOf(IllegalStateException.class);
    }

}
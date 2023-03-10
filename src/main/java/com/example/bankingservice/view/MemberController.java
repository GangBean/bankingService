package com.example.bankingservice.view;

import com.example.bankingservice.domain.entity.member.Member;
import com.example.bankingservice.exception.member.DuplicateLoginIdException;
import com.example.bankingservice.service.AccountService;
import com.example.bankingservice.service.FriendService;
import com.example.bankingservice.service.MemberService;
import com.example.bankingservice.view.dto.AccountDto;
import com.example.bankingservice.view.dto.FriendDto;
import com.example.bankingservice.view.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    private final FriendService friendService;

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<MemberDto> joinMember(@RequestBody MemberDto inputDto) {
        try {
            return new ResponseEntity<>(memberService.join(inputDto), HttpStatus.CREATED);
        } catch (DuplicateLoginIdException e) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{id}/friends")
    public ResponseEntity<FriendDto> addFriend(@PathVariable Long id,
        @RequestBody FriendDto inputDto) {
        try {
            inputDto.setMember(memberService.findById(id).asMember());
            return new ResponseEntity<>(friendService.addFriend(inputDto), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<FriendDto> readFriends(@PathVariable Long id,
        @RequestBody FriendDto friendDto) {
        try {
            friendDto.setMember(Member.builder().id(id).build());
            return new ResponseEntity<>(friendService.readFriends(friendDto), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/accounts")
    public ResponseEntity<AccountDto> readAccounts(@PathVariable Long id,
        @RequestBody AccountDto accountDto) {
        try {
            accountDto.setMember(Member.builder().id(id).build());
            return new ResponseEntity<>(accountService.readAccounts(accountDto), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

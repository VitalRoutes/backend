package swyg.vitalroutes.post.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import swyg.vitalroutes.post.dto.BoardDTO;

@Controller
@RequestMapping("/board") // 부모 주소 자동 입력
public class BoardController {
    @GetMapping("/save") // 자식 주소 매핑
    public String saveForm() {
        //System.out.println("\n============\nsave.html로 이동\n============\n");
        return "save"; // save.html 반환 (게시글 저장 페이지)
    }
    /*
    @PostMapping("/save") // 클라이언트로부터 post로 /save주소로 요청을 받음
    public String save(@ModelAttribute BoardDTO boardDTO) throws IOException { // html에서 Controller로 전달해줄 때, 가장 간단한 방법은 @RequestParam() 방법이 있다. 여기서는 대신 @ModelAttribute 사용
        // @ModelAttribute에 의해 BoardDTO boardDTO클래스 객체를 찾아서
        // save.html의 name들과 BoardDTO의 필드값이 동일하다면 Spring이 해당하는 필드에 대한 Setter호출해 html에 담긴값을 Setter method에 알아서 담아줌
        System.out.println("\n============\nindex.html로 이동\n============\n");
        System.out.println("boardDTO = " + boardDTO); // 들어온 값 확인
        boardService.save(boardDTO);
        return "index";
    }
    */
}

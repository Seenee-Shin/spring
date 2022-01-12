package edu.kh.fin.board.model.exception;

public class UpdateBoardFailException extends RuntimeException {

	public UpdateBoardFailException() {
		super("게시글 수정 과정에 문제발생");
	}
	public UpdateBoardFailException(String message) {
		super(message);
	}
	
}

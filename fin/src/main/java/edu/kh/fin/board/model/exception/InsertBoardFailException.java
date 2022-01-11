package edu.kh.fin.board.model.exception;

public class InsertBoardFailException extends RuntimeException {

	public InsertBoardFailException() {
		super("게시글 삽입 과정에 문제발생");
	}
	public InsertBoardFailException(String message) {
		super(message);
	}
	
}

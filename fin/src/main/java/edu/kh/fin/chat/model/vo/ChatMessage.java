package edu.kh.fin.chat.model.vo;

public class ChatMessage {
	private int chatMessageNo;
	private String message;
	private String createDate;
	private int chatRoomNo;
	private int memberNo;
	
	private String memberName;
	private String memberId;
	
	public ChatMessage() {
		// TODO Auto-generated constructor stub
	}

	public int getChatMessageNo() {
		return chatMessageNo;
	}

	public void setChatMessageNo(int chatMessageNo) {
		this.chatMessageNo = chatMessageNo;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public int getChatRoomNo() {
		return chatRoomNo;
	}

	public void setChatRoomNo(int chatRoomNo) {
		this.chatRoomNo = chatRoomNo;
	}

	public int getMemberNo() {
		return memberNo;
	}

	public void setMemberNo(int memberNo) {
		this.memberNo = memberNo;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	@Override
	public String toString() {
		return "ChatMessage [chatMessageNo=" + chatMessageNo + ", message=" + message + ", createDate=" + createDate
				+ ", chatRoomNo=" + chatRoomNo + ", memberNo=" + memberNo + ", memberName=" + memberName + ", memberId="
				+ memberId + "]";
	}
	
	
	
}

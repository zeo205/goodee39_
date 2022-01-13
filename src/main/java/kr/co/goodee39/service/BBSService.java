package kr.co.goodee39.service;

import java.util.Arrays;
import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;

import com.google.gson.Gson;

import kr.co.goodee39.vo.BBSVO;
import kr.co.goodee39.vo.FileVO;

@Service
public class BBSService {
	
	@Autowired
	SqlSessionTemplate sqlSessionTemplate;
	
	public void selectBBSList(Model model, int num, String title, String content) {
		BBSVO vo = new BBSVO();
		vo.setStart((num-1)*vo.getCount());
		if(!title.equals("")) {
			model.addAttribute("title", title);
			vo.setTitle("%"+title+"%");
		}
		if(!content.equals("")) {
			model.addAttribute("content", content);
			vo.setContent("%"+content+"%");
		}
		model.addAttribute("list", sqlSessionTemplate.selectList("bbs.selectBBSList", vo));
		model.addAttribute("count", sqlSessionTemplate.selectOne("bbs.selectBBSCount", vo));
		model.addAttribute("num", num);
	}
	
	public void selectBBS(Model model, BBSVO vo) {
		//vo = sqlSessionTemplate.selectOne("bbs.selectBBS", vo);
		BBSVO vo2 = sqlSessionTemplate.selectOne("bbs.selectBBS", vo);
		//System.out.println(vo2.toString());
		vo.setNum(vo2.getNum());
		vo.setTitle(vo2.getTitle());
		vo.setContent(vo2.getContent());
		vo.setOwnerid(vo2.getOwnerid());
		vo.setOwnername(vo2.getOwnername());
		vo.setCreatedate(vo2.getCreatedate());
		
		FileVO fvo = new FileVO();
		fvo.setBnum(vo2.getNum());
		
		List<FileVO> filelist = sqlSessionTemplate.selectList("file.selectFile",fvo);
		model.addAttribute("filelist", filelist);
	}
	
	@Transactional
	public void insertBBS(BBSVO vo) {
		Gson gson = new Gson();
		
		FileVO[] fileArray = gson.fromJson(vo.getFilelist(), FileVO[].class);
		List<FileVO> fileList = Arrays.asList(fileArray);
		
		sqlSessionTemplate.insert("bbs.insertBBS", vo);
		
		System.out.println(vo.getNum());
		
		for (FileVO fileVO : fileList) {
			fileVO.setBnum(vo.getNum());
			sqlSessionTemplate.insert("file.insertFile", fileVO);
		}
	}
	
	public void deleteBBS(BBSVO vo) {
		sqlSessionTemplate.delete("bbs.deleteBBS", vo);
	}

	public void updateBBS(BBSVO vo) {
		Gson gson = new Gson();
		
		FileVO[] fileArray = gson.fromJson(vo.getFilelist(), FileVO[].class);
		
		sqlSessionTemplate.update("bbs.updateBBS", vo);
		
		if(fileArray != null) {
			List<FileVO> fileList = Arrays.asList(fileArray);
			System.out.println(vo.getNum());
		
			for (FileVO fileVO : fileList) {
				fileVO.setBnum(vo.getNum());
				sqlSessionTemplate.insert("file.insertFile", fileVO);
			}
		}
	}

	public void deleteBBSFileAll(FileVO fvo) {
//		FileVO fvo = new FileVO();
//		fvo.setBnum(bnum);
		sqlSessionTemplate.delete("file.deleteFile", fvo);
	}
	
	@Transactional
	public void deleteBBSFile(FileVO[] fvos) {
//		FileVO fvo = new FileVO();
//		fvo.setNum(num);
		for (FileVO fvo : fvos) {
			sqlSessionTemplate.delete("file.deleteFile", fvo);
		}
	}
}








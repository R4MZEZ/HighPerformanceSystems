package ru.itmo.fileservice.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.itmo.fileservice.model.entity.FileEntity;
import ru.itmo.fileservice.repository.FileRepository;

@Service
@RequiredArgsConstructor
public class FileService {

	private final FileRepository fileRepository;

	@Transactional
	public void save(MultipartFile file, String username) throws IOException {
		Optional<FileEntity> optionalFileEntity = getFileByUsername(username);
		FileEntity fileEntity = optionalFileEntity.orElseGet(FileEntity::new);
		fileEntity.setContentType(file.getContentType());
		fileEntity.setData(file.getBytes());
		fileEntity.setSize(file.getSize());
		fileEntity.setUsername(username);

		fileRepository.save(fileEntity);
	}

	public Optional<FileEntity> getFileById(Long id) {
		return fileRepository.findById(id);
	}

	@Transactional
	public Optional<FileEntity> getFileByUsername(String username) {
		return fileRepository.findByUsername(username);
	}

	public List<FileEntity> getAllFiles() {
		return fileRepository.findAll();
	}
}

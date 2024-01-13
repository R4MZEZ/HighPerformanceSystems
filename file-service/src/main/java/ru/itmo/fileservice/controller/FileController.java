package ru.itmo.fileservice.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.itmo.fileservice.model.entity.FileEntity;
import ru.itmo.fileservice.service.FileService;
import ru.itmo.fileservice.utils.JwtUtils;

@RestController("files")
@RequiredArgsConstructor
@Tag(name = "Файловый менеджер")
public class FileController {

	private final FileService fileService;
	private final JwtUtils jwtUtils;

	@PostMapping("/upload")
	@Operation(summary = "Загрузить файл", description = "Загружает в базу файл с указанием автора файла")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Успешно"),
		@ApiResponse(responseCode = "500", description = "Ошибка загрузки файла")})
	public ResponseEntity<String> upload(
		@RequestParam("file") @Parameter(description = "Файл для загрузки") MultipartFile file,
		@RequestHeader("Authorization") @Parameter(description = "Токен для дополнительной проверки") String token) {
		try {
			String username = jwtUtils.getUsernameFromHeader(token);
			fileService.save(file, username);
			return ResponseEntity.status(HttpStatus.OK)
				.body(String.format("File uploaded successfully: %s", file.getOriginalFilename()));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(String.format("Could not upload the file: %s! %s", file.getOriginalFilename(),
					e.getMessage()));
		}
	}

//	@GetMapping
//	public List<FileEntity> list() {
//		return fileService.getAllFiles();
//	}


	@GetMapping("/get")
	@Operation(summary = "Получить файл", description = "Выдает файл, принадлежащий автору")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Успешно"),
		@ApiResponse(responseCode = "404", description = "У данного автора нет файлов")})
	public ResponseEntity<byte[]> getFile(@RequestHeader("Authorization") String token) {
		String username = jwtUtils.getUsernameFromHeader(token);
		Optional<FileEntity> fileEntityOptional = fileService.getFileByUsername(username);

		if (fileEntityOptional.isEmpty()) {
			return ResponseEntity.notFound()
				.build();
		}

		FileEntity fileEntity = fileEntityOptional.get();
		return ResponseEntity.ok()
			.header(
				HttpHeaders.CONTENT_DISPOSITION, "attachment;")
			.contentType(MediaType.valueOf(fileEntity.getContentType()))
			.body(fileEntity.getData());
	}
}


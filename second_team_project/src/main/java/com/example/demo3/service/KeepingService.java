package com.example.demo3.service;

import com.example.demo3.dto.BookDTO;
import com.example.demo3.model.BookEntity;
import com.example.demo3.persistence.BookRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<BookDTO> getAllBooks() {
        List<BookEntity> books = bookRepository.findAll();
        return books.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public BookDTO getBookById(int bookId) {
        BookEntity book = bookRepository.findById(bookId).orElse(null);
        return (book != null) ? convertToDTO(book) : null;
    }

    public BookDTO addBook(BookDTO bookDTO, MultipartFile file) {
        Optional<BookEntity> existingBook = bookRepository.findByISBN(bookDTO.getISBN());
        if (existingBook.isPresent()) {
            throw new RuntimeException("ISBN 넘버 " + bookDTO.getISBN() + "은 이미 등록되었습니다");
        }

        // 파일 저장 처리
        if (file != null) {

            String filePath = saveFile(file);
            bookDTO.setBookImgUrl(filePath);
        } else {
            // 이미지 파일이 제공되지 않으면 기존 이미지 URL을 유지
        }

        BookEntity book = convertToEntity(bookDTO, file);
        BookEntity savedBook = bookRepository.save(book);
        return convertToDTO(savedBook);
    }

    public BookDTO updateBook(int bookId, BookDTO bookDTO, MultipartFile file) {
        BookEntity existingBook = bookRepository.findById(bookId).orElse(null);
        String existingImageUrl = existingBook.getBookImgUrl();
        if (existingBook != null) {
            if (file != null) {
                String filePath = saveFile(file);
                bookDTO.setBookImgUrl(filePath);
                if (existingImageUrl != null && !existingImageUrl.equals(filePath)) {
                    deleteFile(existingImageUrl);
                }
            } else {
                // 이미지 파일이 제공되지 않으면 기존 이미지 URL을 유지
                bookDTO.setBookImgUrl(existingBook.getBookImgUrl());
            }
            BeanUtils.copyProperties(bookDTO, existingBook, "bookId");
            bookRepository.save(existingBook);
            return convertToDTO(existingBook);
        }
        return null;
    }

    public boolean deleteBook(int bookId) {
        if (bookRepository.existsById(bookId)) {
            BookEntity existingBook = bookRepository.findById(bookId).orElse(null);
            if (existingBook != null) {
                // 게시글 삭제 시 이미지 파일 삭제
                String imageUrl = existingBook.getBookImgUrl();
                if (imageUrl != null) {
                    deleteFile(imageUrl);
                }
                bookRepository.deleteById(bookId);
                return true;
            }
            return true;
        }
        return false;
    }

    public List<BookEntity> getSearchList(String keyword) {
        // Retrieve all books
        List<BookEntity> allBooks = bookRepository.findAll(); // Assuming this method exists

        // Filter books based on keyword
        return allBooks.stream()
                .filter(book -> book.getBookName().contains(keyword) || book.getDescription().contains(keyword))
                .collect(Collectors.toList());
    }
    private BookEntity convertToEntity(BookDTO bookDTO, MultipartFile file) {
        // 파일 저장 처리
        String filePath = (file != null) ? file.getOriginalFilename() : "no-image01.gif";
        return BookEntity.builder()
                .bookId(bookDTO.getBookId())
                .ISBN(bookDTO.getISBN())
                .bookName(bookDTO.getBookName())
                .bookImgUrl(filePath)
                .publisher(bookDTO.getPublisher())
                .author(bookDTO.getAuthor())
                .publishDate(bookDTO.getPublishDate())
                .genre(bookDTO.getGenre())
                .pages(bookDTO.getPages())
                .description(bookDTO.getDescription())
                .stock(bookDTO.getStock())
                .totalQuantity(bookDTO.getTotalQuantity())
                .build();
    }
    private BookDTO convertToDTO(BookEntity book) {
        return BookDTO.builder()
                .bookId(book.getBookId())
                .ISBN(book.getISBN())
                .bookName(book.getBookName())
                .bookImgUrl(book.getBookImgUrl())
                .publisher(book.getPublisher())
                .author(book.getAuthor())
                .publishDate(book.getPublishDate())
                .genre(book.getGenre())
                .pages(book.getPages())
                .description(book.getDescription())
                .stock(book.getStock())
                .totalQuantity(book.getTotalQuantity())
                .build();
    }

    private String saveFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();

        try {
            Path path = Paths.get("src/main/resources/static/files/" + fileName);
            Files.write(path, file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileName;
    }
    private void deleteFile(String fileName) {
        Path path = Paths.get("src/main/resources/static/files/" + fileName);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BookDTO getBookByISBN(String ISBN) {
        BookEntity book = bookRepository.findByISBN(ISBN).orElse(null);
        return (book != null) ? convertToDTO(book) : null;
    }
    public void saveBooksFromCSV() {
        String filePath = "src/main/resources/data/book4_clean.csv";
        List<BookEntity> books = new ArrayList<>();

        try (Reader reader = new InputStreamReader(new FileInputStream(filePath), "UTF-8");
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            for (CSVRecord record : csvParser) {
                try {
                    BookEntity book = BookEntity.builder()
                            .ISBN(record.get(5))  // ISBN 필드를 String으로 그대로 사용
                            .bookName(record.get(1))  // title
                            .bookImgUrl(record.get(9))  // book_img_url
                            .publisher(record.get(3))  // publisher
                            .author(record.get(2))  // author
                            .publishDate(record.get(4))  // publishDate
                            .genre(record.get(7))  // genre
                            .pages(Integer.parseInt(record.get(6)))  // pages
                            .description(record.get(8))  // description
                            .stock(Integer.parseInt(record.get(10)))  // stock
                            .totalQuantity(Integer.parseInt(record.get(11)))  // totalQuantity
                            .build();
                    books.add(book);
                } catch (NumberFormatException e) {
                    System.out.println("can't turn into Int: " + e.getMessage() + " in line: " + record);
                }
            }
            bookRepository.saveAll(books);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isBooksTableEmpty() {
        return bookRepository.count() == 0;
    }

    private final String apiKey = "af528ebbcc89187904b5aedbcd43dc9fdf67cb043dee213f6280ba7a46097ef5";
    private final String baseUrl = "https://www.nl.go.kr/NL/search/openApi/search.do";

    public BookDTO fetchBookDataByISBN(String ISBN) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("key", apiKey)
                .queryParam("detailSearch", "true")  // 상세 검색 활성화
                .queryParam("isbnOp", "isbn")        // ISBN 검색 옵션 추가
                .queryParam("isbnCode", ISBN)
                .toUriString();

        RestTemplate restTemplate = new RestTemplate();
        String centralLibraryResponse = restTemplate.getForObject(url, String.class);
        System.out.println("XML Response: " + centralLibraryResponse);

        try {
            return parseCentralLibraryResponse(centralLibraryResponse, ISBN);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private BookDTO parseCentralLibraryResponse(String centralLibraryResponse, String ISBN) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new ByteArrayInputStream(centralLibraryResponse.getBytes()));

        document.getDocumentElement().normalize();

        NodeList itemList = document.getElementsByTagName("item");
        if (itemList.getLength() > 0) {
            Node itemNode = itemList.item(0);

            if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                Element itemElement = (Element) itemNode;

                String bookTitle = getTagValue("title_info", itemElement);
                String author = getTagValue("author_info", itemElement);
                String publisher = getTagValue("pub_info", itemElement);
                String publishDate = getTagValue("pub_year_info", itemElement);
                String category = getTagValue("kdc_name_1s", itemElement);

                return BookDTO.builder()
                        .ISBN(ISBN)
                        .bookName(bookTitle)
                        .author(author)
                        .publisher(publisher)
                        .publishDate(publishDate)
                        .genre(category)
                        .build();
            }
        }
        return null;
    }

    private String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = nodeList.item(0);
        return node != null ? node.getNodeValue() : null;
    }
}

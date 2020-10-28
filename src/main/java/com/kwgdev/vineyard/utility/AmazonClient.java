package com.kwgdev.vineyard.utility;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * created by kw on 10/27/2020 @ 5:20 AM
 */

// AmazonS3 is a class from amazon dependency.
//        All other fields are just a representation of variables from our application.yml file.
//        The @Value annotation will bind application properties directly to class fields during application initialization.
//
//        We added method initializeAmazon() to set amazon credentials to amazon client.
//        Annotation @PostConstruct is needed to run this method after constructor will be called,
//        because class fields marked with @Value annotation is null in the constructor.

@Service
public class AmazonClient {

    private AmazonS3 s3client;

    @Value("${amazonProperties.endpointUrl}")
    private String endpointUrl;    @Value("${amazonProperties.bucketName}")
    private String bucketName;    @Value("${amazonProperties.accessKey}")
    private String accessKey;    @Value("${amazonProperties.secretKey}")
    private String secretKey;@PostConstruct
    private void initializeAmazon() {
        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
        this.s3client = new AmazonS3Client(credentials);
    }

    // helper methods

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    private String generateFileName(MultipartFile multiPart) {
        return new Date().getTime() + "-" + multiPart.getOriginalFilename().replace(" ", "_");
    }

    private void uploadFileTos3bucket(String fileName, File file) {
        s3client.putObject(new PutObjectRequest(bucketName, fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
    }

    // delete post image
    public String deletePostImageFromS3Bucket(String postImageName) {
        String fileName = "images/posts/" + postImageName + ".png";
        s3client.deleteObject(new DeleteObjectRequest(bucketName, fileName));
        return "Successfully deleted";
    }

    // delete user profile image
    public String deleteProfilePicFromS3Bucket(String userImageId) {
        String fileName = "images/users/" + userImageId + ".png";
        s3client.deleteObject(new DeleteObjectRequest(bucketName, fileName));
        return "Successfully deleted";
    }

    // upload post image
    public String uploadPostImage(MultipartFile multipartFile, String postImageName) {

//        String fileName = "";
        try {
            File file = convertMultiPartToFile(multipartFile);
//            String fileName = generateFileName(multipartFile);
//            postImageName = endpointUrl + "/" + bucketName + "/images/posts/" + postImageName + ".png";
            postImageName = postImageName + ".png";
            uploadFileTos3bucket("images/posts/" + postImageName, file);
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred. Photo not saved!";
        }

//        System.out.println("Photo saved successfully!");
        return "Photo saved successfully!";
    }

    // upload new profile image
    public String uploadProfilePic(MultipartFile multipartFile, String userImageIdString) {

//        String fileName = "";
        try {
            File file = convertMultiPartToFile(multipartFile);
//            String fileName = generateFileName(multipartFile);
//            postImageName = endpointUrl + "/" + bucketName + "/images/users/" + userImageIdString + ".png";
            userImageIdString = userImageIdString + ".png";
            uploadFileTos3bucket("images/users/" + userImageIdString, file);
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred. Photo not saved!";
        }

//        System.out.println("Photo saved successfully!");
        return "Photo saved successfully!";
    }

    // upload profile image
    public String uploadDefaultProfileImage(String profileImageName) {

        // default prof pic stored in S3 used for every new user
        String profile = "profile.png";
        try {

            // get stored default prof pic and convert it to inputstream
            InputStream in = s3client.getObject(bucketName, profile).getObjectContent();

            // convert stored prof pic input stream to file
            try {
                File file = new File("src/main/resources/static/images/users/temp/profile.png");
                copyInputStreamToFile(in, file);

                profileImageName = profileImageName + ".png";

                // save file as new user profile pic --> this image can be changed later by user
                uploadFileTos3bucket("images/users/" + profileImageName, file);

                in.close();
                file.delete();
            } catch (Exception e) {
                e.printStackTrace();
                return "Error converting profile picture";
            }





        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred. Default Profile Photo not saved!";
        }

//        System.out.println("Photo saved successfully!");
        return "Default Profile photo saved successfully!";
    }

    // helper methods

    // InputStream -> File
    private static void copyInputStreamToFile(InputStream inputStream, File file)
            throws IOException {

        try (FileOutputStream outputStream = new FileOutputStream(file)) {

            int read;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }

            // commons-io
            //IOUtils.copy(inputStream, outputStream);

        }

    }
}

package com.coffee.app.security;

import com.nimbusds.jose.jwk.RSAKey;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class JwtKeyStore {
   private static final Logger log = LoggerFactory.getLogger(JwtKeyStore.class);
   private static final String DIR = System.getProperty("user.home") + "/.coffeeshop";
   private static final String ACCESS_PRI;
   private static final String ACCESS_PUB;
   private static final String REFRESH_PRI;
   private static final String REFRESH_PUB;
   private static final String ACCESS_KID = "coffee-access-key-v1";
   private static final String REFRESH_KID = "coffee-refresh-key-v1";

   public RSAKey loadOrGenerateAccessKey() {
      return this.loadOrGenerate(ACCESS_PRI, ACCESS_PUB, "coffee-access-key-v1");
   }

   public RSAKey loadOrGenerateRefreshKey() {
      return this.loadOrGenerate(REFRESH_PRI, REFRESH_PUB, "coffee-refresh-key-v1");
   }

   private RSAKey loadOrGenerate(String privateKeyPath, String publicKeyPath, String kid) {
      File priFile = new File(privateKeyPath);
      File pubFile = new File(publicKeyPath);
      if (priFile.exists() && pubFile.exists()) {
         try {
            RSAKey key = this.loadFromDisk(priFile, pubFile, kid);
            log.info("[JwtKeyStore] Loaded existing RSA key '{}' from disk", kid);
            return key;
         } catch (Exception e) {
            log.warn("[JwtKeyStore] Failed to load key '{}' from disk ({}); regenerating.", kid, e.getMessage());
         }
      }

      return this.generateAndSave(priFile, pubFile, kid);
   }

   private RSAKey loadFromDisk(File priFile, File pubFile, String kid) throws Exception {
      byte[] priBytes = this.readFile(priFile);
      byte[] pubBytes = this.readFile(pubFile);
      KeyFactory kf = KeyFactory.getInstance("RSA");
      PrivateKey privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(priBytes));
      PublicKey publicKey = kf.generatePublic(new X509EncodedKeySpec(pubBytes));
      return (new RSAKey.Builder((RSAPublicKey)publicKey)).privateKey(privateKey).keyID(kid).build();
   }

   private RSAKey generateAndSave(File priFile, File pubFile, String kid) {
      try {
         log.info("[JwtKeyStore] Generating new RSA key pair '{}' and saving to disk", kid);
         (new File(DIR)).mkdirs();
         KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
         kpg.initialize(2048);
         KeyPair keyPair = kpg.generateKeyPair();
         this.writeFile(priFile, keyPair.getPrivate().getEncoded());
         this.writeFile(pubFile, keyPair.getPublic().getEncoded());
         log.info("[JwtKeyStore] RSA key '{}' saved → {} / {}", new Object[]{kid, priFile, pubFile});
         return (new RSAKey.Builder((RSAPublicKey)keyPair.getPublic())).privateKey(keyPair.getPrivate()).keyID(kid).build();
      } catch (IOException | NoSuchAlgorithmException e) {
         throw new RuntimeException("[JwtKeyStore] Failed to generate or save RSA key: " + kid, e);
      }
   }

   private byte[] readFile(File f) throws IOException {
      FileInputStream fis = new FileInputStream(f);

      byte[] var3;
      try {
         var3 = fis.readAllBytes();
      } catch (Throwable var6) {
         try {
            fis.close();
         } catch (Throwable var5) {
            var6.addSuppressed(var5);
         }

         throw var6;
      }

      fis.close();
      return var3;
   }

   private void writeFile(File f, byte[] data) throws IOException {
      FileOutputStream fos = new FileOutputStream(f);

      try {
         fos.write(data);
      } catch (Throwable var7) {
         try {
            fos.close();
         } catch (Throwable var6) {
            var7.addSuppressed(var6);
         }

         throw var7;
      }

      fos.close();
   }

   static {
      ACCESS_PRI = DIR + "/access-private.der";
      ACCESS_PUB = DIR + "/access-public.der";
      REFRESH_PRI = DIR + "/refresh-private.der";
      REFRESH_PUB = DIR + "/refresh-public.der";
   }
}

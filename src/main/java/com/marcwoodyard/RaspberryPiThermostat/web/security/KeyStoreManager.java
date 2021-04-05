package com.marcwoodyard.RaspberryPiThermostat.web.security;

import com.marcwoodyard.RaspberryPiThermostat.utils.ProgramSettings;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class KeyStoreManager {

    // TODO Replace RSA with Elliptic Curve, might be legal issues --> https://www.bouncycastle.org/specifications.html

    private static String keystoreFile = "keystore.pfx";


    public KeyStoreManager() {
        if (!new File(keystoreFile).exists()) {
            System.out.println("[INFO] Generating SSL Certificate...");

            try {
                GeneratedCert rootCA = createCertificate("Raspberry Pi Root", null, null, true);
                GeneratedCert issuer = createCertificate("Raspberry Pi CA", null, rootCA, true);
                GeneratedCert domain = createCertificate("Raspberry Pi", "localhost", issuer, false);

                String password = ProgramSettings.getWebPassword();
                KeyStore keyStore = KeyStore.getInstance("PKCS12");
                keyStore.load(null, password.toCharArray());
                keyStore.setKeyEntry("WebInterface", domain.privateKey, password.toCharArray(), new X509Certificate[]{domain.certificate, issuer.certificate, rootCA.certificate});
                FileOutputStream store = new FileOutputStream(keystoreFile);
                keyStore.store(store, password.toCharArray());
                System.out.println("[DONE] Generating SSL Certificates.");
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

    /**
     * @param cnName The CN={name} of the certificate. When the certificate is for a domain it should be the domain name
     * @param domain Nullable. The DNS domain for the certificate.
     * @param issuer Issuer who signs this certificate. Null for a self-signed certificate
     * @param isCA   Can this certificate be used to sign other certificates
     * @return Newly created certificate with its private key
     */
    private static GeneratedCert createCertificate(String cnName, String domain, GeneratedCert issuer, boolean isCA) throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(4096, new SecureRandom());
        KeyPair certKeyPair = keyGen.generateKeyPair();

        X500Name name = new X500Name("CN=" + cnName);
        // TODO Better way to generating serial numbers for certs.
        BigInteger serialNumber = BigInteger.valueOf(System.currentTimeMillis());
        Instant validFrom = Instant.now();
        Instant validUntil = validFrom.plus(10 * 360, ChronoUnit.DAYS);

        // If there is no issuer, we self-sign our certificate.
        X500Name issuerName;
        PrivateKey issuerKey;

        if (issuer == null) {
            issuerName = name;
            issuerKey = certKeyPair.getPrivate();
        } else {
            issuerName = new X500Name(issuer.certificate.getSubjectDN().getName());
            issuerKey = issuer.privateKey;
        }

        // The cert builder to build up our certificate information
        JcaX509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(
                issuerName,
                serialNumber,
                Date.from(validFrom), Date.from(validUntil),
                name, certKeyPair.getPublic());

        // Make the cert to a Cert Authority to sign more certs when needed
        if (isCA)
            builder.addExtension(Extension.basicConstraints, true, new BasicConstraints(isCA));

        // Modern browsers demand the DNS name entry
        if (domain != null)
            builder.addExtension(Extension.subjectAlternativeName, false, new GeneralNames(new GeneralName(GeneralName.dNSName, domain)));

        // Sign the certificate
        ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSA").build(issuerKey);
        X509CertificateHolder certHolder = builder.build(signer);
        X509Certificate cert = new JcaX509CertificateConverter().getCertificate(certHolder);

        return new GeneratedCert(certKeyPair.getPrivate(), cert);
    }

    // To create a certificate chain we need the issuers certificate and private key. Keep these together to pass around
    final static class GeneratedCert {
        public final PrivateKey privateKey;
        public final X509Certificate certificate;

        public GeneratedCert(PrivateKey privateKey, X509Certificate certificate) {
            this.privateKey = privateKey;
            this.certificate = certificate;
        }
    }

    public static boolean deleteKeystore() {
        return new File(keystoreFile).delete();
    }
}

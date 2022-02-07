package com.datastax.astra.sdk.config;

import static com.datastax.astra.sdk.utils.AstraRc.readRcVariable;
import static com.datastax.stargate.sdk.utils.Assert.hasLength;
import static com.datastax.stargate.sdk.utils.Assert.notNull;
import static com.datastax.stargate.sdk.utils.Utils.readEnvVariable;

import java.io.File;
import java.io.Serializable;
import java.util.Map;

import org.apache.hc.client5.http.config.RequestConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.astra.sdk.utils.AstraRc;
import com.datastax.oss.driver.api.core.ConsistencyLevel;
import com.datastax.oss.driver.api.core.config.ProgrammaticDriverConfigLoaderBuilder;
import com.datastax.oss.driver.api.core.tracker.RequestTracker;
import com.datastax.stargate.sdk.audit.ApiInvocationObserver;
import com.datastax.stargate.sdk.config.StargateClientConfig;
import com.datastax.stargate.sdk.utils.AnsiUtils;
import com.evanlennick.retry4j.config.RetryConfig;

/**
 * Helper and configurer for Astra.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class AstraClientConfig implements Serializable {
    
    /** Serial. */
    private static final long serialVersionUID = 6950028057943051050L;
    
    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AstraClientConfig.class);
   
    /** Initialize parameters from Environment variables. */
    public static final String ASTRA_DB_ID                = "ASTRA_DB_ID";
    /** ENV VAR to get current or default region (local datacenter). */
    public static final String ASTRA_DB_REGION            = "ASTRA_DB_REGION";
    /** ENV VAR to get part of the token: client Id. */
    public static final String ASTRA_DB_CLIENT_ID         = "ASTRA_DB_CLIENT_ID";
    /** ENV VAR to get part of the token: client Secret. */
    public static final String ASTRA_DB_CLIENT_SECRET     = "ASTRA_DB_CLIENT_SECRET";
    /** ENV VAR to get part of the token: application token. */
    public static final String ASTRA_DB_APPLICATION_TOKEN = "ASTRA_DB_APPLICATION_TOKEN";
    /** ENV VAR to get the keyspace to be selected. */
    public static final String ASTRA_DB_KEYSPACE          = "ASTRA_DB_KEYSPACE";
    /** SECURE BUNDLE FOR EACH RECGIONS. */
    public static final String ASTRA_DB_SCB_FOLDER        = "ASTRA_DB_SCB_FOLDER";
    /** User home folder. */
    public static final String ENV_USER_HOME              = "user.home";

    /** Port for grpc in Astra. */
    public static final int GRPC_PORT                     = 443;
    
    // ------------------------------------------------
    // ------------- Credentials ----------------------
    // ------------------------------------------------
   
    /** Token to authenticate. */
    private String token;
    
    /** Client identifier. */
    private String clientId;
    
    /** Client secret. */
    private String clientSecret;
   
    /**
     * Provide token.
     *
     * @param applicationToken
     *            token
     * @return self reference
     */
    public AstraClientConfig withToken(String applicationToken) {
        hasLength(applicationToken, "applicationToken");
        this.token = applicationToken;
        this.stargateConfig.withApiToken(token);
        return this;
    }
    
    /**
     * Provide clientId.
     *
     * @param clientId
     *            clientId
     * @return self reference
     */
    public AstraClientConfig withClientId(String clientId) {
        hasLength(clientId, "clientId");
        this.clientId = clientId;
        return this;
    }
    
    /**
     * Provide clientSecret.
     *
     * @param clientSecret
     *            clientSecret
     * @return self reference
     */
    public AstraClientConfig withClientSecret(String clientSecret) {
        hasLength(clientSecret, "clientSecret");
        this.clientSecret = clientSecret;
        return this;
    }
    
    /**
     * Getter accessor for attribute 'token'.
     *
     * @return
     *       current value of 'token'
     */
    public String getToken() {
        return token;
    }

    /**
     * Getter accessor for attribute 'clientId'.
     *
     * @return
     *       current value of 'clientId'
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Getter accessor for attribute 'clientSecret'.
     *
     * @return
     *       current value of 'clientSecret'
     */
    public String getClientSecret() {
        return clientSecret;
    }
    
    
    // ------------------------------------------------
    // ---------------- Database ----------------------
    // ------------------------------------------------
   
    /** Attribute to describe the Astra instance. */
    private String databaseId;
    
    /** First and main region to use (others are failing over). */
    private String databaseRegion;
    
    /**
     * Provider database identifier
     *
     * @param databaseId
     *            databaseId
     * @return self reference
     */
    public AstraClientConfig withDatabaseId(String databaseId) {
        hasLength(databaseId, "databaseId");
        this.databaseId = databaseId;
        return this;
    }
    
    /**
     * Provider database identifier
     *
     * @param databaseRegion
     *            databaseRegion
     * @return self reference
     */
    public AstraClientConfig withDatabaseRegion(String databaseRegion) {
        hasLength(databaseRegion, "databaseRegion");
        this.databaseRegion = databaseRegion;
        return this;
    }
    
    /**
     * Getter accessor for attribute 'databaseId'.
     *
     * @return
     *       current value of 'databaseId'
     */
    public String getDatabaseId() {
        return databaseId;
    }

    /**
     * Getter accessor for attribute 'databaseRegion'.
     *
     * @return
     *       current value of 'databaseRegion'
     */
    public String getDatabaseRegion() {
        return databaseRegion;
    }
    
    // ------------------------------------------------
    // ---------------- Stargate ----------------------
    // ------------------------------------------------
     
    /** Configuring Stargate to work in Astra. */
    private StargateClientConfig stargateConfig;
    
    /**
     * Getter accessor for attribute 'stargateConfig'.
     *
     * @return
     *       current value of 'stargateConfig'
     */
    public StargateClientConfig getStargateConfig() {
        return stargateConfig;
    }
    
    // ------------------------------------------------
    // ------------- HTTP Client ----------------------
    // ------------------------------------------------
    
    /**
     * Enable fine Grained configuration of the HTTP Client.
     *
     * @param reqConfig
     *            request configuration
     * @return self reference
     */
    public AstraClientConfig withHttpRequestConfig(RequestConfig reqConfig) {
        stargateConfig.withHttpRequestConfig(reqConfig);
        return this;
    }
    
    /**
     * Enable fine Grained configuration of the HTTP Retries.
     *
     * @param retryConfig
     *            retry configuration
     * @return self reference
     */
    public AstraClientConfig withHttpRetryConfig(RetryConfig retryConfig) {
        stargateConfig.withHttpRetryConfig(retryConfig);
        return this;
    }
    
    /**
     * Api Invocations trigger some events processed in observer.
     * 
     * @param name
     *            unique identiier
     * @param observer
     *            instance of your Observer
     * @return self reference
     */
    public AstraClientConfig addHttpObserver(String name, ApiInvocationObserver observer) {
        stargateConfig.addHttpObserver(name, observer);
        return this;
    }
    
    /**
     * Api Invocations trigger some events processed in observer.
     * 
     * @param observers
     *           observers lists
     * @return self reference
     */
    public AstraClientConfig withHttpObservers(Map<String, ApiInvocationObserver> observers) {
        stargateConfig.withHttpObservers(observers);
        return this;
    }

    // ------------------------------------------------
    // ----------------- Grpc -------------------------
    // ------------------------------------------------
    
    /**
     * Enable gRPC
     * 
     * @return reference enforcing cqlsession disabled
     */
    public AstraClientConfig enableGrpc() {
        stargateConfig.enableGrpc();
        return this;
    }
    
    // ------------------------------------------------
    // -------------- CqlSession ----------------------
    // ------------------------------------------------
    
    /** Enable SCB download to target folder. */
    private boolean downloadSecureConnectBundle = true;
    
    /** Folder to load secure connect bundle with formated names scb_dbid_region.zip */
    private String secureConnectBundleFolder  = System.getProperty(ENV_USER_HOME) + File.separator + ".astra";
    
    /**
     * Getter accessor for attribute 'secureConnectBundleFolder'.
     *
     * @return
     *       current value of 'secureConnectBundleFolder'
     */
    public String getSecureConnectBundleFolder() {
        return secureConnectBundleFolder;
    }
    
    /**
     * Getter for downloadSecureConnectBundle.
     *
     * @return
     *      downloadSecureConnectBundle value
     */
    public boolean isEnabledDownloadSecureConnectBundle() {
        return downloadSecureConnectBundle;
    }
    
    /**
     * Enable SCB downloads.
     * 
     * @return
     *      current reference.
     */
    public AstraClientConfig enableDownloadSecureConnectBundle() {
        this.downloadSecureConnectBundle = true;
        return this;
    }
    
    /**
     * Disable SCB downloads.
     * 
     * @return
     *      current reference.
     */
    public AstraClientConfig disableDownloadSecureConnectBundle() {
        this.downloadSecureConnectBundle = false;
        return this;
    }
    
    /**
     * Enable CqlSession
     * 
     * @return reference enforcing cqlsession disabled
     */
    public AstraClientConfig enableCql() {
        stargateConfig.enableCql();
        return this;
    }
    
    /**
     * Populate Secure connect bundle
     *
     * @param scbFile
     *            path of cloud secure bundle
     * @return current reference
     */
    public AstraClientConfig withCqlCloudSecureConnectBundle(String scbFile) {
        stargateConfig.withCqlCloudSecureConnectBundle(scbFile);
        return this;
    }
    
    /**
     * Populate config.
     * 
     * @param conf
     *      configuration
     * @return
     *      current reference
     */
    public AstraClientConfig withCqlDriverConfig(ProgrammaticDriverConfigLoaderBuilder conf) {
        stargateConfig.withCqlDriverConfigLoaderBuilder(conf);
        return this;
    }
    
    /**
     * Populate Consistency level
     *
     * @param cl
     *           consistency level
     * @return current reference
     */
    public AstraClientConfig withCqlConsistencyLevel(ConsistencyLevel cl) {
        stargateConfig.withCqlConsistencyLevel(cl);
        return this;
    }
    
    /**
     * Populate configuration file
     *
     * @param configFile
     *          configuration file
     * @return current reference
     */
    public AstraClientConfig withCqlDriverConfigurationFile(File configFile) {
        stargateConfig.withCqlDriverConfigurationFile(configFile);
        return this;
    }
    
    /**
     * Fill Keyspaces.
     *
     * @param keyspace
     *            keyspace name
     * @return current reference
     */
    public AstraClientConfig withCqlKeyspace(String keyspace) {
        stargateConfig.withCqlKeyspace(keyspace);
        return this;
    }
    
    /**
     * Provide a metrics registry.
     * 
     * @param mr
     *       metrics registry
     * @return
     *      self reference
     */
    public AstraClientConfig withCqlMetricsRegistry(Object mr) {
        stargateConfig.withCqlMetricsRegistry(mr);
        return this;
    }
    
    /**
     * Provide a request tracker.
     *
     * @param rt
     *       request tracker
     * @return
     *      self reference
     */
    public AstraClientConfig withCqlRequestTracker(RequestTracker rt) {
        stargateConfig.withCqlRequestTracker(rt);
        return this;
    }
    
    /**
     * Provide clientSecret.
     *
     * @param scbPath
     *            secure bundle
     * @return self reference
     */
    public AstraClientConfig withCqlSecureConnectBundleFolder(String scbPath) {
        hasLength(scbPath, "scbPath");
        this.secureConnectBundleFolder = scbPath;
        return this;
    }
    
    /**
     * Helper to build the path (use multiple times).
     * 
     * @param dId
     *      database id
     * @param dbRegion
     *      database region
     * @return
     *      filename
     */
    public static String buildScbFileName(String dId, String dbRegion) {
        return AstraClient.SECURE_CONNECT + dId + "_" + dbRegion + ".zip";
    }
    
    // ------------------------------------------------
    // -------------------- Core ----------------------
    // ------------------------------------------------
    
    /**
     * Reading environment variables
     */
    public AstraClientConfig() {
        LOGGER.info("Initializing [" + AnsiUtils.yellow("AstraClient") + "]");
        
        // Loading Stargate Environment variable
        stargateConfig = new StargateClientConfig();
        
        // Loading ~/.astrarc section default if present
        if (AstraRc.exists()) {
            loadFromAstraRc(AstraRc.load(), AstraRc.ASTRARC_DEFAULT);
        }
        
        // Authentication
        readEnvVariable(ASTRA_DB_APPLICATION_TOKEN).ifPresent(this::withToken);
        readEnvVariable(ASTRA_DB_CLIENT_ID).ifPresent(this::withClientId);
        readEnvVariable(ASTRA_DB_CLIENT_SECRET).ifPresent(this::withClientSecret);
        readEnvVariable(ASTRA_DB_SCB_FOLDER).ifPresent(this::withCqlSecureConnectBundleFolder);
        
        // Database
        readEnvVariable(ASTRA_DB_ID).ifPresent(this::withDatabaseId);
        readEnvVariable(ASTRA_DB_REGION).ifPresent(this::withDatabaseRegion);
        readEnvVariable(ASTRA_DB_KEYSPACE).ifPresent(this::withCqlKeyspace);
    }
    
    // ------------------------------------------------
    // ----------------- AstraRC ----------------------
    // ------------------------------------------------
    
    /**
     * Some settings can be loaded from ~/.astrarc in you machine.
     * 
     * @param arc AstraRc
     * @param sectionName String
     * @return AstraClientBuilder
     */
    public AstraClientConfig loadFromAstraRc(AstraRc arc, String sectionName) {
        notNull(arc, "AstraRc");
        hasLength(sectionName, "sectionName");
        readRcVariable(arc, ASTRA_DB_ID,            sectionName).ifPresent(this::withDatabaseId);
        readRcVariable(arc, ASTRA_DB_REGION,        sectionName).ifPresent(this::withDatabaseRegion);
        readRcVariable(arc, ASTRA_DB_APPLICATION_TOKEN, sectionName).ifPresent(this::withToken);
        readRcVariable(arc, ASTRA_DB_CLIENT_ID,     sectionName).ifPresent(this::withClientId);
        readRcVariable(arc, ASTRA_DB_CLIENT_SECRET, sectionName).ifPresent(this::withClientSecret);
        readRcVariable(arc, ASTRA_DB_KEYSPACE,      sectionName).ifPresent(this::withCqlKeyspace);
        readRcVariable(arc, ASTRA_DB_SCB_FOLDER,    sectionName).ifPresent(this::withCqlSecureConnectBundleFolder);
        return this;
    }
    
    /**
     * Final build.
     * @return
     *      return the target object
     */
    public AstraClient build() {
        return new AstraClient(this);
    }

   

    

   

}

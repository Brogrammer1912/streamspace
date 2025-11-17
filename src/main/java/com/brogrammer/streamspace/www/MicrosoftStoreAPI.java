package com.brogrammer.streamspace.www;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.ArrayList;
import java.util.Date;

@HttpExchange("https://apps.microsoft.com/api/Reco/GetMovieProductsList?gl=US&hl=en-us&listName=")
public interface MicrosoftStoreAPI {

    /** Sample URL
     *  https://apps.microsoft.com/api/Reco/GetMovieProductsList?gl=US&hl=en-us&listName=video.newreleases.movies&pgNo=1&noItems=24&mediaType=movies&filteredCategories=AllProducts&studioFilter=
     */

    @GetExchange("video.newreleases.movies&pgNo={pgNo}&noItems={noItems}&mediaType={mediaType}&filteredCategories={filteredCategories}&studioFilter=")
    MicrosoftStoreRecord newReleases(@PathVariable(value = "pgNo") int pgNo,
                                     @PathVariable(value = "noItems") int noItems,
                                     @PathVariable(value = "mediaType") String mediaType,
                                     @PathVariable(value = "filteredCategories") String filteredCategories);

    /** Available Filters
     *  Action/Adventure
     *  Animation
     *  Anime
     *  Comedy
     *  Documentary
     *  Drama
     *  Family
     *  Foreign/Independent
     *  Horror
     *  Other
     *  Romance
     *  Romantic Comedy
     *  Sci-Fi/Fantasy
     *  Sports
     *  Thriller/Mystery
     *  TV Movies
     */
    // Sample for applying Filters
    // @GetExchange("video.newreleases.movies&pgNo=1&noItems=12&mediaType=movies&filteredCategories=Comedy&studioFilter=")
    // MicrosoftStoreRecord newTopComedy();

    @GetExchange("video.toprated.movies&pgNo={pgNo}&noItems={noItems}&mediaType={mediaType}&filteredCategories={filteredCategories}&studioFilter=")
    MicrosoftStoreRecord topRated(@PathVariable(value = "pgNo") int pgNo,
                                  @PathVariable(value = "noItems") int noItems,
                                  @PathVariable(value = "mediaType") String mediaType,
                                  @PathVariable(value = "filteredCategories") String filteredCategories);

    @GetExchange("video.topselling.movies&pgNo={pgNo}&noItems={noItems}&mediaType={mediaType}&filteredCategories={filteredCategories}&studioFilter=")
    MicrosoftStoreRecord topSelling(@PathVariable(value = "pgNo") int pgNo,
                                    @PathVariable(value = "noItems") int noItems,
                                    @PathVariable(value = "mediaType") String mediaType,
                                    @PathVariable(value = "filteredCategories") String filteredCategories);

    @GetExchange("video.toprented.movies&pgNo={pgNo}&noItems={noItems}&mediaType={mediaType}&filteredCategories={filteredCategories}&studioFilter=")
    MicrosoftStoreRecord topRented(@PathVariable(value = "pgNo") int pgNo,
                                   @PathVariable(value = "noItems") int noItems,
                                   @PathVariable(value = "mediaType") String mediaType,
                                   @PathVariable(value = "filteredCategories") String filteredCategories);

    @GetExchange("video.collections.promo_marvelmovies&pgNo=1&noItems=48&mediaType=movies&filteredCategories=AllProducts")
    MicrosoftStoreRecord MCUCollection();

    @JsonIgnoreProperties(ignoreUnknown = true)
    record MicrosoftStoreRecord (
            String title,
            String description,
            CuratedCollectionDetails curatedCollectionDetails,
            ArrayList<ProductsList> productsList,
            Object highlightedList,
            Object cursor,
            boolean hasCursorPaging,
            Object filterOptions,
            int totalCount,
            int nextPageNumber,
            boolean hasMorePages,
            Object continuationToken) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record CuratedCollectionDetails (
            Object bgColor,
            String description,
            Object fgColor,
            Object imageUrl,
            String title,
            String itemType) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record ProductsList (
            ArrayList<Preview> previews,
            String subtitleNarratorText,
            String typeTag,
            String ratingCountFormatted,
            String iconUrl,
            String pdpImageUrl,
            Object largePromotionImage,
            String posterArtUrl,
            String boxArtUrl,
            Object heroImageUrl,
            String iconUrlBackground,
            Object trailers,
            ArrayList<Object> screenshots,
            String encodedTitle,
            boolean isMovie,
            boolean isApplication,
            boolean isGame,
            boolean isTvSeries,
            boolean isMoviesOrTVs,
            boolean isPwa,
            boolean isCoreGame,
            boolean isAllowed,
            boolean isBrowsable,
            boolean isAd,
            boolean isPrimeVideo,
            boolean isSparkProduct,
            boolean isAndroid,
            String redirectUrl,
            boolean isPartOfLocaleAssessment,
            boolean isHardware,
            boolean isSubscription,
            Object platforms,
            Object privacyUrl,
            Object additionalTermLinks,
            Object legalUrl,
            boolean accessible,
            boolean isDeviceCompanionApp,
            Object supportUris,
            Object features,
            Object notes,
            Object supportedLanguages,
            Object publisherCopyrightInformation,
            Object publisherAddress,
            Object publisherPhoneNumber,
            Object additionalLicenseTerms,
            Object appWebsiteUrl,
            Object productRatings,
            Object requiredHardware,
            Object recommendedHardware,
            Object hardwareWarnings,
            Object permissionsRequired,
            Object packageAndDeviceCapabilities,
            Object version,
            Date lastUpdateDateUtc,
            Object skus,
            Object osProductInformation,
            Object categoryId,
            Object subcategoryId,
            Object navItemId,
            Object navId,
            Object addOnPriceRange,
            Object recurrencePolicy,
            Object deviceFamilyDisallowedReason,
            Object builtFor,
            Object revisionId,
            Object pdpBackgroundColor,
            boolean containsDownloadPackage,
            Object systemRequirements,
            Object keyIds,
            Object allowedPlatforms,
            Object xbox360ContentMediaId,
            Object earlyAdopterEnrollmentUrl,
            Object installationTerms,
            Object skuDisplayGroups,
            boolean xboxXpa,
            Object relatedPackageIdentities,
            Object primaryPackageIdentity,
            Object detailsDisplayConfiguration,
            Object ownershipType,
            Object offerExpirationDate,
            Object warningMessages,
            Object deviceQualified,
            Object isMicrosoftProduct,
            Object productFamilyLicenseTerms,
            Object hasParentBundles,
            Object hasAlternateEditions,
            Object isLtidCompatible,
            Object productPartD,
            String videoProductType,
            boolean isMsixvc,
            Object position,
            Object parentId,
            Object categoryIds,
            Object mediaBadges,
            Object isEligibleForMoviesAnywhere,
            Object copyrightInformation,
            Installer installer,
            Object catalogSource,
            Object webAppStartUrl,
            ArrayList<String> categories,
            ArrayList<Image> images,
            String productId,
            String title,
            Object shortTitle,
            String subtitle,
            String description,
            Object shortDescription,
            String developerName,
            String publisherName,
            Object publisherId,
            boolean isUniversal,
            Object language,
            Object bgColor,
            Object fgColor,
            double price,
            String displayPrice,
            Object strikethroughPrice,
            Object recentLowestPriceMessage,
            Object displayPricePrefix,
            Object buyActionTitleOverride,
            Object buyActionSubtitle,
            boolean currencyMismatch,
            Object promoMessage,
            Object promoEndDateUtc,
            double averageRating,
            int ratingCount,
            boolean hasFreeTrial,
            Object productType,
            String productFamilyName,
            Object mediaType,
            ArrayList<Object> contentIds,
            ArrayList<Object> packageFamilyNames,
            Object recommendationReason,
            Object releaseNotes,
            Object subcategoryName,
            Object alternateId,
            Object alternateIds,
            Object curatedBGColor,
            Object curatedFGColor,
            Object curatedImageUrl,
            Object curatedTitle,
            Object curatedDescription,
            Object doNotFilter,
            String collectionItemType,
            Object curatedVideoUri,
            Object creativeId,
            Object payloadId,
            Object contentType,
            Object artistName,
            Object artistId,
            Object albumTitle,
            Object albumProductId,
            Object isExplicit,
            int numberOfSeasons,
            Date releaseDateUtc,
            int durationInSeconds,
            Object isCompatible,
            Object isSoftBlocked,
            Object isPurchaseEnabled,
            Object incompatibleReason,
            boolean developerOptOutOfSDCardInstall,
            boolean hasAddOns,
            boolean hasThirdPartyIAPs,
            Object externalUri,
            Object autosuggestSubtitle,
            Object merchandizedProductType,
            Object voiceTitle,
            Object phraseCustomPronunciation,
            Object plaintextPassName,
            Object glyphTextPassName,
            Object subscriptionDiscountMessageTemplate,
            Object capabilitiesTable,
            Object capabilities,
            boolean hideFromCollections,
            Object isDownloadable,
            boolean hideFromDownloadsAndUpdates,
            Object incompatibleLink,
            Object incompatibleLabel,
            boolean gamingOptionsXboxLive,
            Object actionOverrides,
            String availableDevicesDisplayText,
            String availableDevicesNarratorText,
            Object models,
            Object capabilityXboxEnhanced,
            ArrayList<Badge> badges,
            Object catalogId,
            Object hasParentBundle,
            Object acquiredDateUtc,
            Object approximateSizeInBytes,
            Object maxInstallSizeInBytes,
            Object bundleIds,
            Object productActionsList,
            ArrayList<SkusSummary> skusSummary,
            Object colorPicker,
            Object bundlePackageIdentities,
            Object pcgaShortDescription,
            Object pcgaTrailer,
            Object pcgaMinimumUserAge,
            boolean isGamingAppOnly,
            Object installerType,
            Object appExtension,
            boolean supportsInstantGaming,
            boolean requireDeviceRegistration,
            String schema) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Badge (
            Object deferredRequestKey,
            String styleKey,
            String text,
            String type) {
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    record Image (
            String imageType,
            String backgroundColor,
            String foregroundColor,
            String caption,
            String imagePositionInfo,
            Object productColor,
            String url,
            int height,
            int width) {
    }

//@JsonIgnoreProperties(ignoreUnknown = true)
//record Image2 (
//        String imageType,
//        String backgroundColor,
//        String foregroundColor,
//        String caption,
//        String imagePositionInfo,
//        Object productColor,
//        String url,
//        int height,
//        int width) {
//}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Installer (
            String type,
            String id,
            Object extras,
            Object installerErrorUrl,
            Object installerErrors) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Preview (
            String $type,
            String imageType,
            String backgroundColor,
            String foregroundColor,
            String caption,
            String imagePositionInfo,
            Object productColor,
            String url,
            int height,
            int width,
            Object title,
            String videoPurpose,
            Object audioEncoding,
            Object videoEncoding,
            Object deepLink,
            Image image,
            int bitrate,
            Object videoPositionInfo,
            Object overlayFiles,
            Object sortOrder) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record SalePrice (
            Object conditions,
            double price,
            String displayPrice,
            String badgeId) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record SkusSummary (
            String skuId,
            Object skuDisplayRanks,
            Object skuTitle,
            Object description,
            Object images,
            Object badges,
            Object colorHexCode,
            Object colorDisplayName,
            double msrp,
            String displayMSRP,
            ArrayList<SalePrice> salePrices,
            Object includedWith,
            StreamingService streamingService) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record StreamingService (
            String id,
            String displayName,
            Object installer,
            Object deepLink,
            Object productRef,
            Object packageFamilyName,
            String consumptionModel,
            Object channelName) {
    }
}


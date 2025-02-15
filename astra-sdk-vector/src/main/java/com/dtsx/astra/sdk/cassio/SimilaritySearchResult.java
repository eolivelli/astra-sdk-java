package com.dtsx.astra.sdk.cassio;

import lombok.Data;

/**
 * Item Retrieved by the search.
 *
 * @param <EMBEDDED>
 *       record.
 */
@Data
public class SimilaritySearchResult<EMBEDDED> {

    /**
     * Embedded object
     */
    private EMBEDDED embedded;

    /**
     * Score
     */
    private float similarity;

}

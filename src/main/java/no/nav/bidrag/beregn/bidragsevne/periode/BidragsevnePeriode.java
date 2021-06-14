package no.nav.bidrag.beregn.bidragsevne.periode;

import java.util.List;
import no.nav.bidrag.beregn.bidragsevne.beregning.BidragsevneBeregning;
import no.nav.bidrag.beregn.bidragsevne.bo.BeregnBidragsevneGrunnlag;
import no.nav.bidrag.beregn.bidragsevne.bo.BeregnBidragsevneResultat;
import no.nav.bidrag.beregn.felles.bo.Avvik;

public interface BidragsevnePeriode {
    BeregnBidragsevneResultat beregnPerioder(
        BeregnBidragsevneGrunnlag beregnBidragsevneGrunnlag);
    List<Avvik> validerInput(BeregnBidragsevneGrunnlag beregnBidragsevneGrunnlag);
    static BidragsevnePeriode getInstance() {
        return new BidragsevnePeriodeImpl(BidragsevneBeregning.getInstance());
    }
}

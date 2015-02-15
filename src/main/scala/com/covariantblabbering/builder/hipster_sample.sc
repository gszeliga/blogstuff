import com.covariantblabbering.builder.ApplicativeStyleWithMultipleMessages.SmartBuilderOps._

import com.covariantblabbering.builder.Hipster
import com.covariantblabbering.builder.HipsterRules._

(Hipster
  @> facialHairStyle("Bushy beard")
  @> tshirt("Heather Grey's V-Neck")
  @> acceptedBands("Sun Kil Moon")
  @> acceptedHobbies("Knitting")) toEither


//http://stackoverflow.com/questions/3862717/scala-curried-constructors